package de.janhoelscher.jms.database.users.web.management;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import de.janhoelscher.jms.database.users.User;
import de.janhoelscher.jms.database.users.web.login.UserLoginProvider;
import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
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
		Response response = null;
		if (request.getMethod() == Method.POST) {
			response = processPostRequest(request);
		}
		if (response != null) {
			return response;
		}
		if (user.hasPermission(UserManagementHttpHandler.MANAGEMENT_PERMISSION)) {
			return NanoHTTPD.newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, getManagementSite(request));
		}
		return NanoHTTPD.newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, getChangeNameSite(request));
	}

	private String getManagementSite(Request request) throws IOException {
		String site =
					IOUtils.toString(getClass().getResourceAsStream("/de/janhoelscher/jms/www/management/user_management.html"), Charset.defaultCharset());
		return site.replace("%currentname%", UserLoginProvider.getUser(request).getName());
	}

	private String getChangeNameSite(Request request) throws IOException {
		String site =
					IOUtils.toString(getClass().getResourceAsStream("/de/janhoelscher/jms/www/management/change_name.html"), Charset.defaultCharset());
		return site.replace("%currentname%", UserLoginProvider.getUser(request).getName());
	}

	private Response processPostRequest(Request request) {
		String type = request.getPostData().get("type");
		if (type == null) {
			return null;
		}
		switch (type) {
			case "changename":
				return processChangeNameRequest(request);
			default:
				return null;
		}
	}

	private Response processChangeNameRequest(Request request) {
		String newName = request.getPostData().get("newname");
		if (newName == null) {
			return null;
		}
		UserLoginProvider.getUser(request).setName(newName.trim());
		return null;
	}
}