package de.janhoelscher.jms.database.media;

import java.sql.Date;

import de.janhoelscher.jms.database.users.User;

public class Episode {
	private final int	id;

	private final int	number;

	private Season		season;

	private Series		series;

	private Thumbnail	thumbnail;

	protected Episode(int id, int number) {
		this.id = id;
		this.number = number;
	}

	public int getId() {
		return id;
	}

	public int getNumber() {
		return number;
	}

	public Season getSeason() {
		if (season == null) {
			return MediaDatabase.getSeason(this);
		}
		return season;
	}

	public Series getSeries() {
		if (series == null) {
			return MediaDatabase.getSeries(this);
		}
		return series;
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

	public EpisodeWatchState getWatchState(User user) {
		return MediaDatabase.getWatchState(user, this);
	}

	public class EpisodeWatchState extends WatchState<Episode> {
		protected EpisodeWatchState(User user, Episode target, Date lastWatched, long pausedAt, boolean finishedOnce) {
			super(user, target, lastWatched, pausedAt, finishedOnce);
		}
	}
}