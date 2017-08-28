package de.janhoelscher.jms;

import java.io.InputStream;

import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class MainHttpHandler implements HttpRequestHandler {

	private static final String	MIME_JS		= "text/js";

	private static final String	MIME_CSS	= "text/css";

	@Override
	public boolean isLoginNeeded(Request request) {
		return true;
	}

	@Override
	public Response handleHttpRequest(Request request) throws Exception {
		if (request.getRequestUri().getLastPart(1).equals("resources")) {
			if (request.getRequestUri().getLastPart().equals("css")) {
				return NanoHTTPD.newChunkedResponse(Status.OK, MainHttpHandler.MIME_CSS, getCssInputStream());
			} else if (request.getRequestUri().getLastPart().equals("js")) {
				return NanoHTTPD.newChunkedResponse(Status.OK, MainHttpHandler.MIME_JS, getJsInputStream());
			}
		} else {
			return NanoHTTPD.newChunkedResponse(Status.OK, NanoHTTPD.MIME_HTML, getMainPageInputStream());
		}
		return null;
	}

	private InputStream getMainPageInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/main.html");
	}

	private InputStream getCssInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/webresource/global.css");
	}

	private InputStream getJsInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/webresource/global.js");
	}
}