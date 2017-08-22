package de.janhoelscher.jms.web.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.janhoelscher.jms.web.http.HttpConstants;
import fi.iki.elonen.NanoHTTPD;

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
			return getRequestHandler(session.getUri()).handleHttpRequest(session);
		} catch (Exception e) {
			return NanoHTTPD.newFixedLengthResponse("ERROR");
		}
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
		if (!path.startsWith(HttpConstants.SLASH + "")) {
			path = HttpConstants.SLASH + path;
		}
		return path;
	}
}