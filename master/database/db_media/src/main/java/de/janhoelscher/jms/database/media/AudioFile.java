package de.janhoelscher.jms.database.media;

public class AudioFile extends MediaFile {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1380173474323895045L;

	private final long			duration;

	private final int			bitrate;

	protected AudioFile(int id, int libraryId, String path, long size, long duration, int bitrate) {
		super(id, libraryId, path, size);
		this.duration = duration;
		this.bitrate = bitrate;
	}

	public long getDuration() {
		return duration;
	}

	public int getBitrate() {
		return bitrate;
	}
}