package de.janhoelscher.jms.database.media;

import java.util.Arrays;

public class Season {

	private final int	id;

	private final int	number;

	private Series		series;

	private Episode[]	episodes;

	protected Season(int id, int number) {
		this.id = id;
		this.number = number;
	}

	public int getId() {
		return id;
	}

	public int getNumber() {
		return number;
	}

	public Series getSeries() {
		if (series == null) {
			return MediaDatabase.getSeries(this);
		}
		return series;
	}

	public Episode[] getEpisodes() {
		if (episodes == null) {
			MediaDatabase.getEpisodes(this);
		}
		return Arrays.copyOf(episodes, episodes.length);
	}
}