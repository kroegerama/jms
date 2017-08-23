package de.janhoelscher.jms.database.media;

public class ImageFile implements MediaFile {

	private final int		id;

	private final String	file;

	private long			size;

	private int				width;

	private int				height;

	public ImageFile(int id, String file, long size, int width, int height) {
		this.id = id;
		this.file = file;
		this.size = size;
		this.width = width;
		this.height = height;
	}

	public int getId() {
		return id;
	}

	public String getFile() {
		return file;
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
}