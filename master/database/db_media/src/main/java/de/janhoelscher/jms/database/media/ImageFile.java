package de.janhoelscher.jms.database.media;

public class ImageFile extends MediaFile {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 8976559162637091873L;

	private final int			width;

	private final int			height;

	protected ImageFile(int id, int libraryId, String path, long size, int width, int height) {
		super(id, libraryId, path, size);
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}