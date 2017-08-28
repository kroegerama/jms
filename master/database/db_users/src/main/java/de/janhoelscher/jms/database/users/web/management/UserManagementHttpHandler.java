package de.janhoelscher.jms.database.users.web.management;

import java.io.InputStream;

import de.janhoelscher.jms.database.users.User;
import de.janhoelscher.jms.database.users.web.login.UserLoginProvider;
import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class UserManagementHttpHandler implements HttpRequestHandler {

	private static final String MANAGEMENT_PERMISSION = "users.manage";

	@Override
	public boolean isLoginNeeded(Request request) {
		return true;
	}

	@Override
	public Response handleHttpRequest(Request request) throws Exception {
		User user = UserLoginProvider.getUser(request);
		if (user.hasPermission(UserManagementHttpHandler.MANAGEMENT_PERMISSION)) {
			return NanoHTTPD.newChunkedResponse(Status.OK, NanoHTTPD.MIME_HTML, getManagementInputStream());
		} else {
			return NanoHTTPD.newChunkedResponse(Status.OK, NanoHTTPD.MIME_HTML, getChangeNameInputStream());
		}
	}

	private InputStream getManagementInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/management/management.html");
	}

	private InputStream getChangeNameInputStream() {
		return getClass().getResourceAsStream("/de/janhoelscher/jms/www/management/change_name.html");
	}
}