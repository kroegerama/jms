package de.janhoelscher.jms.database.users.web.login;

import java.io.InputStream;

import de.janhoelscher.jms.database.users.User;
import de.janhoelscher.jms.database.users.UserDatabase;
import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class LoginHttpHandler implements HttpRequestHandler {

	@Override
	public boolean isLoginNeeded(Request request) {
		return false;
	}

	@Override
	public Response handleHttpRequest(Request request) throws Exception {
		InputStream in;
		if (request.getRequestUri().getLastPart().equals("css")) {
			in = getCssInputStream();
		} else if (request.getRequestUri().getLastPart().equals("js")) {
			in = getJsInputStream();
		} else {
			in = getHtmlInputStream();
		}
		return NanoHTTPD.newChunkedResponse(Status.OK, NanoHTTPD.MIME_HTML, in);
	}

	private InputStream getHtmlInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/login/login.html");
	}

	private InputStream getCssInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/login/login.css");
	}

	private InputStream getJsInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/login/login.js");
	}

	protected static boolean checkLogin(Request request) {
		if (request.getMethod() != Method.POST) {
			return false;
		}
		try {
			String username = request.getPostData().get("username");
			String password = request.getPostData().get("password");
			User user = UserDatabase.getUser(username);
			if (UserDatabase.passwordMatches(user, password)) {
				UserLoginProvider.setUser(request, user);
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}