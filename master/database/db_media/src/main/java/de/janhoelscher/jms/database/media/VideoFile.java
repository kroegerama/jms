package de.janhoelscher.jms.database.media;

public class VideoFile implements MediaFile {

	private int		id;

	private String	file;

	private long	duration;

	private long	size;

	private int		width;

	private int		height;

	private float	framerate;

	private String	extractedAudioFile;

	public VideoFile(int id, String file, long duration, long size, int width, int height, float framerate, String extractedAudioFile) {
		this.id = id;
		this.file = file;
		this.duration = duration;
		this.size = size;
		this.width = width;
		this.height = height;
		this.framerate = framerate;
		this.extractedAudioFile = extractedAudioFile;
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getFramerate() {
		return framerate;
	}

	public String getExtractedAudioFile() {
		return extractedAudioFile;
	}

	public void setExtractedAudioFile(String extractedAudioPath) {
		if (this.extractedAudioFile == null || !this.extractedAudioFile.equals(extractedAudioPath)) {
			this.extractedAudioFile = extractedAudioPath;
			MediaDatabase.updateExtractedAudioFile(this);
		}
	}

	@Override
	public String toString() {
		return "VideoFile [file=" + file + ", duration=" + duration + ", size=" + size + ", width=" + width + ", height=" + height + ", framerate=" + framerate + "]";
	}
}