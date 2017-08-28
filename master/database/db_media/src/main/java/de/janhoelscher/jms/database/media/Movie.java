package de.janhoelscher.jms.database.media;

import java.sql.Date;

import de.janhoelscher.jms.database.users.User;

public class Movie {

	private final int		id;

	private final String	name;

	private final String	regisseur;

	private Publisher		publisher;

	private VideoFile		file;

	private Thumbnail		thumbnail;

	protected Movie(int id, String name, String regisseur) {
		this.id = id;
		this.name = name;
		this.regisseur = regisseur;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRegisseur() {
		return regisseur;
	}

	public Publisher getPublisher() {
		if (publisher == null) {
			return MediaDatabase.getPublisher(this);
		}
		return publisher;
	}

	public VideoFile getFile() {
		if (file == null) {
			return MediaDatabase.getVideoFile(this);
		}
		return file;
	}

	public Thumbnail getThumbnail() {
		if (thumbnail == null) {
			return MediaDatabase.getThumbnail(this);
		}
		return thumbnail;
	}

	public MovieWatchState getWatchState(User user) {
		return MediaDatabase.getWatchState(user, this);
	}

	public class MovieWatchState extends WatchState<Movie> {
		protected MovieWatchState(User user, Movie target, Date lastWatched, long pausedAt, boolean finishedOnce) {
			super(user, target, lastWatched, pausedAt, finishedOnce);
		}
	}
}