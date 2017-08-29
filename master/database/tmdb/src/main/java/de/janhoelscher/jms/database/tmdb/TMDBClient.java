package de.janhoelscher.jms.database.tmdb;

import de.janhoelscher.jms.database.tmdb.data.TMDBSearch;
import de.janhoelscher.jms.database.tmdb.data.TMDBSearchResult;

class TMDBClient {

	public static final long	RESET_TIMESPAN_LENGTH	= 10 * 1000;

	private static TMDBClient	instance;

	public static TMDBClient getInstance() {
		if (instance == null) {
			instance = new TMDBClient();
		}
		return instance;
	}

	private long	resetTimespanEnd	= -1;

	private int		remainingRequests;

	private TMDBClient() {

	}

	public TMDBSearchResult search(TMDBSearch search) {
		consumeRequest();

	}

	private synchronized void consumeRequest() {
		if (remainingRequests > 1) {
			remainingRequests--;
			return;
		}
		sleep(resetTimespanEnd - System.currentTimeMillis());
		remainingRequests = 40;
		resetTimespanEnd = System.currentTimeMillis() + RESET_TIMESPAN_LENGTH;
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignored
		}
	}
}