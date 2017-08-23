package de.janhoelscher.jms.web.server;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public interface HttpRequestHandler {

	public default Response handleHttpRequest(Request request) throws Exception {
		switch (request.getMethod()) {
			case GET:
				return handleGetRequest(request);
			case POST:
				return handlePostRequest(request);
			case HEAD:
				return handleHeadRequest(request);
			case TRACE:
				return handleTraceRequest(request);
			default:
				return NanoHTTPD.newFixedLengthResponse(Status.NOT_IMPLEMENTED, NanoHTTPD.MIME_PLAINTEXT, "501 - Not implemented");
		}
	}

	Response handleGetRequest(Request request);

	Response handlePostRequest(Request request);

	Response handleHeadRequest(Request request);

	Response handleTraceRequest(Request request);
}