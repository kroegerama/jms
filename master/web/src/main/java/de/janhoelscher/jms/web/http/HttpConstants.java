package de.janhoelscher.jms.web.http;

public class HttpConstants {

	public static final char	SPACE				= ' ';
	public static final char	SLASH				= '/';
	public static final char	QUESTIONMARK		= '?';
	public static final char	AND					= '&';
	public static final char	EQUALS				= '=';
	public static final char	COLON				= ':';

	public static final String	NEXT_LINE			= "\r\n";

	public static final String	HTTP				= "HTTP";

	// HTTP VERSIONS
	public static final String	HTTP_VERSION_1		= HttpConstants.HTTP + HttpConstants.SLASH + "1.0";
	public static final String	HTTP_VERSION_1_1	= HttpConstants.HTTP + HttpConstants.SLASH + "1.1";
	public static final String	HTTP_VERSION_2		= HttpConstants.HTTP + HttpConstants.SLASH + "2";

	// HTTP REQUEST METHODS
	public static final String	GET					= "GET";
	public static final String	POST				= "POST";
	public static final String	HEAD				= "HEAD";
	public static final String	PUT					= "PUT";
	public static final String	PATCH				= "PATCH";
	public static final String	DELETE				= "DELETE";
	public static final String	TRACE				= "TRACE";
	public static final String	OPTIONS				= "OPTIONS";
	public static final String	CONNECT				= "CONNECT";

	// GENERAL HTTP HEADERS
	public static final String	CACHE_CONTROL		= "cache-control";

	// HTTP REQUEST HEADERS
	public static final String	HOST				= "host";
	public static final String	ACCEPT				= "accept";
	public static final String	ACCEPT_ENCODING		= "accept-encoding";
	public static final String	ACCEPT_LANGUAGE		= "accept-language";
	public static final String	COOKIE				= "cookie";
	public static final String	REFERER				= "referer";
	public static final String	USER_AGENT			= "user-agent";

	// HTTP RESPONSE HEADERS
	public static final String	ACCEPT_RANGES		= "accept-ranges";
	public static final String	AGE					= "age";
	public static final String	CONTENT_ENCODING	= "content-encoding";
	public static final String	CONTENT_TYPE		= "content-type";
	public static final String	CONTENT_LENGTH		= "content-length";
	public static final String	CONTENT_LANGUAGE	= "content-language";
	public static final String	DATE				= "date";
	public static final String	LAST_MODIFIED		= "last-modified";
	public static final String	LOCATION			= "location";
	public static final String	STATUS				= "status";
	public static final String	CONNECTION			= "connection";
	public static final String	TRANSFER_ENCODING	= "transfer-encoding";

	// HTTP HEADER VALUES
	public static final String	KEEP_ALIVE			= "keep-alive";
}