package de.janhoelscher.jms.database.media;

public class Interpreter {

	private final int		id;

	private final String	name;

	private Thumbnail		thumbnail;

	protected Interpreter(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Thumbnail getThumbnail() {
		if (thumbnail == null) {
			return MediaDatabase.getThumbnail(this);
		}
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
		MediaDatabase.updateThumbnail(this);
	}
}