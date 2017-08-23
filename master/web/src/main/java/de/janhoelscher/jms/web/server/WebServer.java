package de.janhoelscher.jms.web.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import de.janhoelscher.jms.web.http.HttpConstants;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class WebServer extends NanoHTTPD {

	public static final int		PORT			= 80;

	public static final String	DOCUMENT_ROOT	= "www/";

	private static WebServer	instance;

	public static WebServer getInstance() {
		if (WebServer.instance == null) {
			WebServer.instance = new WebServer();
		}
		return WebServer.instance;
	}

	private HashMap<String, HttpRequestHandler> requestHandlers = new HashMap<>();

	public WebServer() {
		super(WebServer.PORT);
		File docRootFile = new File(WebServer.DOCUMENT_ROOT);
		if (!docRootFile.isDirectory()) {
			docRootFile.mkdirs();
		}
		setDefaultRequestHandler(new DefaultHttpRequestHandler());
	}

	@Override
	public void start() throws IOException {
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

	@Override
	public Response serve(IHTTPSession session) {
		try {
			HttpRequestHandler handler = getRequestHandler(session.getUri());
			Request request = Request.init(session);
			Response response = handler.handleHttpRequest(request);
			if (response != null) {
				return response;
			}
		} catch (Exception e) {
			LogFactory.getLog(WebServer.class).warn("Failed to handle request for session " + session);
		}
		return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "500 - Internal Server Error"); // TODO build a better 500 site.
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

	public void setDefaultRequestHandler(HttpRequestHandler requestHandler) {
		requestHandlers.put("/", requestHandler);
	}

	public void resetDefaultRequestHandler() {
		setDefaultRequestHandler(new DefaultHttpRequestHandler());
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

	private String correctPath(String path) {
		if (path == null) {
			throw new NullPointerException("Path cannot be null.");
		}
		if (path.equals("/")) {
			return path;
		}
		if (!path.startsWith(HttpConstants.SLASH + "")) {
			path = HttpConstants.SLASH + path;
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}
}