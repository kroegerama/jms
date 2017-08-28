package de.janhoelscher.jms.database.media;

import java.io.File;

public abstract class MediaFile extends File {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 2979539510325602339L;

	private final int			id;

	private final int			libraryId;

	private final String		path;

	private final long			size;

	protected MediaFile(int id, int libraryId, String path, long size) {
		super(path);
		this.id = id;
		this.libraryId = libraryId;
		this.path = path;
		this.size = size;
	}

	public int getId() {
		return id;
	}

	public int getLibraryId() {
		return libraryId;
	}

	@Override
	public String getPath() {
		return path;
	}

	public long getSize() {
		return size;
	}
}