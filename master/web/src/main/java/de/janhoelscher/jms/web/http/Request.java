package de.janhoelscher.jms.web.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD.CookieHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class Request implements IHTTPSession {

	private static final String CLIENT_ID_COOKIE_NAME = "clientId";

	public static Request init(IHTTPSession session) {
		String clientId = session.getCookies().read(Request.CLIENT_ID_COOKIE_NAME);
		if (clientId == null) {
			clientId = UUID.randomUUID().toString() + UUID.randomUUID().toString();
			session.getCookies().set(Request.CLIENT_ID_COOKIE_NAME, clientId, 1);
		}
		return new Request(session, RequestMetadata.getMetadata(clientId));
	}

	private final IHTTPSession		httpSession;

	private final RequestMetadata	metadata;

	private HttpRequestUri			requestUri;

	private String					rawPostData;

	private Map<String, String>		postData;

	private Request(IHTTPSession httpSession, RequestMetadata metadata) {
		this.httpSession = httpSession;
		this.metadata = metadata;
	}

	public void finalize(Response response) {
		httpSession.getCookies().unloadQueue(response);
	}

	public IHTTPSession getHttpSession() {
		return httpSession;
	}

	public RequestMetadata getMetadata() {
		return metadata;
	}

	public HttpRequestUri getRequestUri() {
		if (requestUri == null) {
			requestUri = HttpRequestUri.fromString(httpSession.getUri());
		}
		return requestUri;
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
		throw new UnsupportedOperationException("Use getPostData to obtain POST data!");
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

	public String getRawPostData() {
		if (rawPostData == null) {
			rawPostData = readPostData();
		}
		return rawPostData;
	}

	public Map<String, String> getPostData() {
		if (postData == null) {
			postData = decodePostData();
		}
		return postData;
	}

	@SuppressWarnings("resource")
	private String readPostData() {
		Scanner scan = new Scanner(httpSession.getInputStream());
		StringBuilder sb = new StringBuilder();
		while (scan.hasNextLine()) {
			sb.append(scan.nextLine());
		}
		return sb.toString();
	}

	private Map<String, String> decodePostData() {
		String[] parts = getRawPostData().split("\\&");
		Map<String, String> result = new HashMap<>();
		for (String part : parts) {
			String[] key_value = part.split("\\=");
			if (key_value.length == 1) {
				result.put(key_value[0], "");
			} else {
				result.put(key_value[0], key_value[1]);
			}
		}
		return result;
	}
}