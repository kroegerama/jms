package de.janhoelscher.jms.web.server;

import java.io.IOException;
import java.util.HashMap;

import de.janhoelscher.jms.logging.Logger;
import de.janhoelscher.jms.web.http.HttpConstants;
import de.janhoelscher.jms.web.http.Request;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class WebServer extends NanoHTTPD {

	public static final int		PORT	= 80;

	private static WebServer	instance;

	public static boolean create(HttpRequestHandler defaultRequestHandler) {
		if (WebServer.instance != null) {
			return false;
		}
		WebServer.instance = new WebServer(defaultRequestHandler);
		return true;
	}

	public static WebServer getInstance() {
		return WebServer.instance;
	}

	private LoginProvider						loginProvider;

	private HashMap<String, HttpRequestHandler>	requestHandlers	= new HashMap<>();

	public WebServer(HttpRequestHandler defaultRequestHandler) {
		super(WebServer.PORT);
		setDefaultRequestHandler(defaultRequestHandler);
	}

	public void setLoginProvider(LoginProvider loginProvider) {
		this.loginProvider = loginProvider;
	}

	public void setDefaultRequestHandler(HttpRequestHandler requestHandler) {
		requestHandlers.put("", requestHandler);
	}

	public void registerRequestHandler(String path, HttpRequestHandler requestHandler) {
		path = correctPath(path);
		if (path.equals(HttpConstants.SLASH + "")) {
			throw new IllegalArgumentException("Cannot overwrite root path. Use setDefaultRequestHandler() to explicitly set a handler fot this path.");
		}
		requestHandlers.put(path, requestHandler);
	}

	public void unregisterRequestHandler(String path) {
		path = correctPath(path);
		if (path.equals(HttpConstants.SLASH + "")) {
			throw new IllegalArgumentException("Cannot overwrite root path. Use setDefaultRequestHandler() to explicitly set a handler fot this path.");
		}
		requestHandlers.remove(path);
	}

	@Override
	public void start() throws IOException {
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

	@Override
	public Response serve(IHTTPSession session) {
		Response response;
		Request request = Request.init(session);
		try {
			HttpRequestHandler handler = getRequestHandler(session.getUri());
			if (handler != null) {
				response = getLoginResponse(handler, request);
				if (response == null) {
					response = handler.handleHttpRequest(request);
				}
			} else {
				response = null;
			}
		} catch (Exception e) {
			Logger.warn("Failed to handle request for session " + session, e);
			response = NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "500 - Internal Server Error"); // TODO build a better 500 site.
		}
		if (response == null) {
			response = NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 - Not found");
		}
		request.finalize(response);
		return response;
	}

	private Response getLoginResponse(HttpRequestHandler handler, Request request) throws Exception {
		if (handler.isLoginNeeded(request)) {
			if (loginProvider == null) {
				return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 - Not found");
			} else {
				if (!loginProvider.isLoggedIn(request)) {
					return loginProvider.getLoginHandler().handleHttpRequest(request);
				}
			}
		}
		return null;
	}

	private HttpRequestHandler getRequestHandler(String path) {
		path = correctPath(path);
		HttpRequestHandler handler = requestHandlers.get(path);
		while (handler == null) {
			int index = path.lastIndexOf(HttpConstants.SLASH);
			if (index < 0) {
				return null;
			}
			path = path.substring(0, index);
			handler = requestHandlers.get(path);
		}
		return handler;
	}

	private String correctPath(String path) {
		if (path == null) {
			throw new NullPointerException("Path cannot be null.");
		}
		if (!path.startsWith(HttpConstants.SLASH + "")) {
			path = HttpConstants.SLASH + path;
		}
		if (path.endsWith(HttpConstants.SLASH + "")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}
}