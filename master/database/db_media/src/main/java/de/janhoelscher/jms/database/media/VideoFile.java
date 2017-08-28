package de.janhoelscher.jms.database.media;

public class VideoFile extends MediaFile {

	/**
	 *
	 */
	private static final long	serialVersionUID	= -7546213430711522073L;

	private final long			duration;

	private final int			width;

	private final int			height;

	private final float			framerate;

	private AudioFile			extractedAudioFile;

	public VideoFile(int id, int libraryId, String path, long size, long duration, int width, int height, float framerate) {
		super(id, libraryId, path, size);
		this.duration = duration;
		this.width = width;
		this.height = height;
		this.framerate = framerate;
	}

	public long getDuration() {
		return duration;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getFramerate() {
		return framerate;
	}

	public AudioFile getExtractedAudioFile() {
		if (extractedAudioFile == null) {
			return MediaDatabase.getExtractedAudioFile(this);
		}
		return extractedAudioFile;
	}

	public void setExtractedAudioFile(AudioFile audioFile) {
		this.extractedAudioFile = audioFile;
		MediaDatabase.updateExtractedAudioFile(this);
	}
}