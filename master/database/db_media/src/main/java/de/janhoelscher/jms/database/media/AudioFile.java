package de.janhoelscher.jms.database.media;

public class AudioFile implements MediaFile {

	private final int		id;

	private final String	file;

	private long			duration;

	private long			size;

	private int				bitrate;

	public AudioFile(int id, String file, long duration, long size, int bitrate) {
		this.id = id;
		this.file = file;
		this.duration = duration;
		this.size = size;
		this.bitrate = bitrate;
	}

	public int getId() {
		return id;
	}

	public String getFile() {
		return file;
	}

	public long getDuration() {
		return duration;
	}

	public long getSize() {
		return size;
	}

	public int getBitrate() {
		return bitrate;
	}
}