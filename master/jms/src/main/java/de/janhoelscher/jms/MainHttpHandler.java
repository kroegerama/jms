package de.janhoelscher.jms;

import java.io.InputStream;

import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class MainHttpHandler implements HttpRequestHandler {

	@Override
	public boolean isLoginNeeded(Request request) {
		return true;
	}

	@Override
	public Response handleHttpRequest(Request request) throws Exception {
		if (request.getRequestUri().getLastPart().equals("somethingtoimplement")) {

		} else {
			return NanoHTTPD.newChunkedResponse(Status.OK, NanoHTTPD.MIME_HTML, getMainPageInputStream());
		}
		return null;
	}

	private InputStream getMainPageInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/main.html");
	}
}