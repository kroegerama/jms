package de.janhoelscher.jms.database.tmdb.data;

import java.util.HashMap;

public class TMDBSearchResult {
	private final int						page;

	private final HashMap<Integer, String>	results;

	private final int						totalPages;

	private final int						totalResults;

	public TMDBSearchResult(int page, HashMap<Integer, String> results, int totalPages, int totalResults) {
		this.page = page;
		this.results = results;
		this.totalPages = totalPages;
		this.totalResults = totalResults;
	}

	public int getPage() {
		return page;
	}

	public HashMap<Integer, String> getResults() {
		return results;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getTotalResults() {
		return totalResults;
	}
}