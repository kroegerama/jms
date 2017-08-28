package de.janhoelscher.jms.database.media;

import java.util.Arrays;

public class Series {

	private final int		id;

	private final String	name;

	private Thumbnail		thumbnail;

	private Season[]		seasons;

	protected Series(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Thumbnail getThumbnail() {
		if (thumbnail == null) {
			return MediaDatabase.getThumbnail(this);
		}
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
		MediaDatabase.updateThumbnail(this);
	}

	public Season[] getSeasons() {
		if (seasons == null) {
			MediaDatabase.getSeasons(this);
		}
		return Arrays.copyOf(seasons, seasons.length);
	}
}