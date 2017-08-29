package de.janhoelscher.jms.database.tmdb.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TMDBSearch {

	public static final String	PARAM_API_KEY	= "api_key";

	public static final String	PARAM_QUERY		= "query";

	public static final String	PARAM_LANGUAGE	= "language";

	public static final String	PARAM_PAGE		= "page";

	private TMDBSearchType		type;

	private Map<String, String>	params;

	public TMDBSearch(TMDBSearchType type, String apiKey) {
		this.type = type;
		params = new HashMap<>();
		setParam(PARAM_API_KEY, apiKey);
	}

	public TMDBSearch(TMDBSearchType type, String apiKey, String query) {
		this.type = type;
		params = new HashMap<>();
		setParam(PARAM_API_KEY, apiKey);
		setParam(PARAM_QUERY, query);
	}

	public TMDBSearch(TMDBSearchType type, String apiKey, String query, int page) {
		this.type = type;
		params = new HashMap<>();
		setParam(PARAM_API_KEY, apiKey);
		setParam(PARAM_QUERY, query);
		setParam(PARAM_PAGE, page + "");
	}

	public TMDBSearch(TMDBSearchType type, String apiKey, String query, int page, String language) {
		this.type = type;
		params = new HashMap<>();
		setParam(PARAM_API_KEY, apiKey);
		setParam(PARAM_QUERY, query);
		setParam(PARAM_PAGE, page + "");
		setParam(PARAM_LANGUAGE, language);
	}

	public TMDBSearch(TMDBSearchType type, Map<String, String> params) {
		this.type = type;
		this.params = params;
	}

	public TMDBSearchType getType() {
		return type;
	}

	public void setType(TMDBSearchType type) {
		this.type = type;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void setParam(String param, String value) {
		params.put(param, value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("/search/");
		sb.append(type.toString().toLowerCase());
		boolean first = true;
		for (Entry<String, String> entry : params.entrySet()) {
			if (first) {
				sb.append('?');
				first = false;
			} else {
				sb.append('&');
			}
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(entry.getValue());
		}
		return sb.toString();
	}

	public static enum TMDBSearchType {
										COMPANY,
										COLLECTION,
										KEYWORD,
										MOVIE,
										MULTI,
										PERSON,
										TV;
	}
}