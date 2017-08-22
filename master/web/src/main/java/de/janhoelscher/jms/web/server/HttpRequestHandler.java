package de.janhoelscher.jms.web.server;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public interface HttpRequestHandler {

	public default Response handleHttpRequest(IHTTPSession session) throws Exception {
		switch (session.getMethod()) {
			case GET:
				return handleGetRequest(session);
			case POST:
				return handlePostRequest(session);
			case HEAD:
				return handleHeadRequest(session);
			case TRACE:
				return handleTraceRequest(session);
			default:
				return NanoHTTPD.newFixedLengthResponse(Status.NOT_IMPLEMENTED, NanoHTTPD.MIME_PLAINTEXT, "501 - Not implemented");
		}
	}

	Response handleGetRequest(IHTTPSession session);

	Response handlePostRequest(IHTTPSession session);

	Response handleHeadRequest(IHTTPSession session);

	Response handleTraceRequest(IHTTPSession session);
}