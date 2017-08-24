package de.janhoelscher.jms.database.users.web.login;

import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import de.janhoelscher.jms.web.server.LoginProvider;

public class UserLoginProvider implements LoginProvider {

	private static final String	LOGGED_IN	= "logged in";

	private static final String	TRUE		= "true";

	private HttpRequestHandler	loginHttpHandler;

	@Override
	public boolean isLoggedIn(Request request) {
		Object loggedInObj = request.getMetadata().getClientMetadata().get(UserLoginProvider.LOGGED_IN);
		boolean loggedIn = loggedInObj != null && loggedInObj.equals(UserLoginProvider.TRUE);
		if (!loggedIn) {
			loggedIn = LoginHttpHandler.checkLogin(request);
			if (loggedIn) {
				request.getMetadata().getClientMetadata().put(UserLoginProvider.LOGGED_IN, UserLoginProvider.TRUE);
			}
		}
		return loggedIn;
	}

	@Override
	public HttpRequestHandler getLoginHandler() {
		if (loginHttpHandler == null) {
			loginHttpHandler = new LoginHttpHandler();
		}
		return loginHttpHandler;
	}
}