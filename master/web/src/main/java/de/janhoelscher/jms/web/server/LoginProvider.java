package de.janhoelscher.jms.web.server;

import de.janhoelscher.jms.web.http.Request;

public interface LoginProvider {

	public boolean isLoggedIn(Request request);

	public HttpRequestHandler getLoginHandler();
}