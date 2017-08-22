package de.janhoelscher.jms.web.server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.janhoelscher.jms.web.http.HttpConstants;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

class DefaultHttpRequestHandler implements HttpRequestHandler {

	@Override
	public Response handleGetRequest(IHTTPSession session) {
		String requestedFilePath = WebServer.DOCUMENT_ROOT + session.getUri();
		File file = new File(requestedFilePath);
		if (file.isDirectory()) {
			requestedFilePath = requestedFilePath + HttpConstants.SLASH + "index.html";
			file = new File(requestedFilePath);
		}
		if (!file.exists()) {
			return NanoHTTPD.newFixedLengthResponse("404");
		}
		if (pathIsForbidden(requestedFilePath)) {
			return NanoHTTPD.newFixedLengthResponse("403");
		}
		try {
			String responseData = FileUtils.readFileToString(file, Charset.defaultCharset());
			return NanoHTTPD.newFixedLengthResponse(responseData);
		} catch (IOException e) {
			return NanoHTTPD.newFixedLengthResponse("500");
		}
	}

	@Override
	public Response handlePostRequest(IHTTPSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response handleHeadRequest(IHTTPSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response handleTraceRequest(IHTTPSession session) {
		// TODO Auto-generated method stub
		return null;

	}

	private boolean pathIsForbidden(String path) {
		try {
			return !FilenameUtils.directoryContains(FilenameUtils.normalize(WebServer.DOCUMENT_ROOT), FilenameUtils.normalize(path));
		} catch (IOException e) {
			return true;
		}
	}
}