package de.janhoelscher.jms.web.http;

import java.util.HashMap;
import java.util.Map.Entry;

public class HttpRequestUri {

	private String					path;

	private String[]				pathParts;

	private HashMap<String, String>	parameters;

	public HttpRequestUri(String path) {
		this(path, new HashMap<>());
	}

	public HttpRequestUri(String baseUri, HashMap<String, String> parameters) {
		this.path = baseUri;
		this.pathParts = path.split("\\/");
		this.parameters = parameters;
	}

	public String getPath() {
		return path;
	}

	public String getPart(int index) {
		return pathParts[index];
	}

	public String getLastPart() {
		return getLastPart(0);
	}

	public String getLastPart(int index) {
		return pathParts[pathParts.length - index - 1];
	}

	public String getParam(String name) {
		return parameters.get(name);
	}

	public HashMap<String, String> getParams() {
		return parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(path);
		boolean first = true;
		for (Entry<String, String> param : parameters.entrySet()) {
			if (first) {
				sb.append('?');
				first = false;
			} else {
				sb.append('&');
			}
			sb.append(param.getKey() + "=" + param.getValue());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HttpRequestUri other = (HttpRequestUri) obj;
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		return true;
	}

	public static HttpRequestUri fromString(String rawUri) {
		int index = rawUri.indexOf(HttpConstants.QUESTIONMARK);
		if (index < 0) {
			return new HttpRequestUri(rawUri);
		}
		String path = rawUri.substring(0, index);
		rawUri = rawUri.substring(index + 1);
		String[] rawParams = rawUri.split("\\" + HttpConstants.AND);
		HashMap<String, String> params = new HashMap<>();
		for (String param : rawParams) {
			String[] key_value = param.split("\\" + HttpConstants.EQUALS);
			params.put(key_value[0], key_value[1]);
		}
		return new HttpRequestUri(path, params);
	}
}