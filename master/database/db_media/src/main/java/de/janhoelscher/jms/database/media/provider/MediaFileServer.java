package de.janhoelscher.jms.database.media.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.janhoelscher.jms.web.http.HttpConstants;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

class MediaFileServer {

	public static Response serveMediaFile(IHTTPSession session, String mimeType, long fileLength, InputStream input) {
		try {
			String rangeStr = session.getHeaders().get("range");
			long[] range = MediaFileServer.getRange(rangeStr);

			if (range[0] >= 0) {
				if (range[0] >= fileLength) {
					Response res = MediaFileServer.createResponse(Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + fileLength);
					return res;
				} else {
					return MediaFileServer.createPartialResponse(input, mimeType, range, fileLength);
				}
			} else {
				Response res = MediaFileServer.createResponse(Response.Status.OK, mimeType, input, fileLength);
				res.setChunkedTransfer(true);
				res.setGzipEncoding(true);
				res.addHeader("Content-Length", "" + fileLength);
				return res;
			}
		} catch (IOException ioe) {
			return MediaFileServer.createResponse(Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN");
		}
	}

	private static Response createPartialResponse(InputStream in, String mimeType, long[] range, long fileLength) throws FileNotFoundException, IOException {
		if (range[1] < 0) {
			range[1] = fileLength - 1;
		}
		long length = range[1] - range[0] + 1;
		if (length < 0) {
			length = 0;
		}

		in.skip(range[0]);
		Response res = MediaFileServer.createResponse(Status.PARTIAL_CONTENT, mimeType, in, fileLength);
		res.addHeader(HttpConstants.CONTENT_LENGTH, "" + length);
		res.addHeader("Content-Range", "bytes " + range[0] + "-" + range[1] + "/" + fileLength);
		res.addHeader(HttpConstants.CONTENT_TYPE, mimeType);
		res.setChunkedTransfer(true);
		res.setGzipEncoding(true);
		return res;
	}

	private static Response createResponse(Response.Status status, String mimeType, String message) {
		Response res = NanoHTTPD.newFixedLengthResponse(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	private static Response createResponse(Response.Status status, String mimeType, InputStream message, long length) {
		Response res = NanoHTTPD.newChunkedResponse(status, mimeType, message);// .newFixedLengthResponse(status, mimeType, message,
																				// length);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	private static long[] getRange(String range) {
		long[] res = new long[] { 0, -1 };
		if (range != null) {
			if (range.startsWith("bytes=")) {
				range = range.substring("bytes=".length());
				if (range.startsWith("-")) {
					res[0] = Long.parseLong(range.substring(1));
				}
				try {
					String[] parts = range.split("\\-");
					res[0] = Long.parseLong(parts[0]);
					if (parts.length > 1) {
						res[1] = Long.parseLong(parts[1]);
					}
				} catch (NumberFormatException e) {
					// ignored
				}
			}
		}
		return res;
	}
}