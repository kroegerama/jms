package de.janhoelscher.jms.database.media;

public class Album {

	private final int		id;

	private final String	name;

	private Interpreter		interpreter;

	private Thumbnail		thumbnail;

	protected Album(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Interpreter getInterpreter() {
		if (interpreter == null) {
			return MediaDatabase.getInterpreter(this);
		}
		return interpreter;
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