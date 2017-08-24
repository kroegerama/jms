package de.janhoelscher.jms.web.server;

import de.janhoelscher.jms.web.http.Request;
import fi.iki.elonen.NanoHTTPD.Response;

public interface HttpRequestHandler {

	public boolean isLoginNeeded(Request request);

	public Response handleHttpRequest(Request request) throws Exception;
}