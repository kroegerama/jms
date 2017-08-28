package de.janhoelscher.jms.database.media;

import java.sql.Date;

import de.janhoelscher.jms.database.users.User;

public abstract class WatchState<T> {

	private final User	user;

	private final T		target;

	private Date		lastWatched;

	private long		pausedAt;

	private boolean		finishedOnce;

	protected WatchState(User user, T target, Date lastWatched, long pausedAt, boolean finishedOnce) {
		this.user = user;
		this.target = target;
		this.lastWatched = lastWatched;
		this.pausedAt = pausedAt;
		this.finishedOnce = finishedOnce;
	}

	public User getUser() {
		return user;
	}

	public T getTarget() {
		return target;
	}

	public Date getLastWatched() {
		return lastWatched;
	}

	public long getPausedAt() {
		return pausedAt;
	}

	public boolean isFinishedOnce() {
		return finishedOnce;
	}
}