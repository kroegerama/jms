package de.janhoelscher.jms.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD.CookieHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class Request implements IHTTPSession {

	private static final String CLIENT_ID_COOKIE_NAME = "clientId";

	public static Request init(IHTTPSession session) {
		String clientId = session.getCookies().read(Request.CLIENT_ID_COOKIE_NAME);
		if (clientId == null) {
			clientId = UUID.randomUUID().toString();
			session.getCookies().set(Request.CLIENT_ID_COOKIE_NAME, clientId, 0);
		}
		return new Request(session, RequestMetadata.getMetadata(clientId));
	}

	private final IHTTPSession		httpSession;

	private final RequestMetadata	metadata;

	private Request(IHTTPSession httpSession, RequestMetadata metadata) {
		this.httpSession = httpSession;
		this.metadata = metadata;
	}

	public IHTTPSession getHttpSession() {
		return httpSession;
	}

	public RequestMetadata getMetadata() {
		return metadata;
	}

	@Override
	public Method getMethod() {
		return httpSession.getMethod();
	}

	@Override
	public String getUri() {
		return httpSession.getUri();
	}

	@Override
	public Map<String, String> getHeaders() {
		return httpSession.getHeaders();
	}

	@Override
	public void execute() throws IOException {
		httpSession.execute();
	}

	@Override
	public CookieHandler getCookies() {
		return httpSession.getCookies();
	}

	@Override
	public InputStream getInputStream() {
		return httpSession.getInputStream();
	}

	@Override
	public Map<String, String> getParms() {
		return httpSession.getParms();
	}

	@Override
	public String getQueryParameterString() {
		return httpSession.getQueryParameterString();
	}

	@Override
	public void parseBody(Map<String, String> files) throws IOException, ResponseException {
		httpSession.parseBody(files);
	}
}