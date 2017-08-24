package de.janhoelscher.jms.database.users.web;

import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD.Response;

public class UserHttpRequestHandler implements HttpRequestHandler {

	@Override
	public boolean isLoginNeeded(Request request) {
		return true;
	}

	@Override
	public Response handleHttpRequest(Request request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}