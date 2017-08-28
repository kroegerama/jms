package de.janhoelscher.jms.database.media;

public class Thumbnail {

	private final int	id;

	private byte[]		imgData;

	private String		source;

	protected Thumbnail(int id) {
		this.id = id;
	}

	protected Thumbnail(int id, String source) {
		this.id = id;
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public byte[] getImgData() {
		return imgData;
	}

	public void setImgData(byte[] imgData) {
		this.imgData = imgData;
		MediaDatabase.updateThumbnailImageData(this);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		if (this.source == null || !this.source.equals(source)) {
			this.source = source;
			MediaDatabase.updateThumbnailSource(this);
		}
	}
}