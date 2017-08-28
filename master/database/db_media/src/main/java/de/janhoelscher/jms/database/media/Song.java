package de.janhoelscher.jms.database.media;

public class Song {

	private final int		id;

	private final String	name;

	private Album			album;

	private Interpreter		interpreter;

	private final String	genre;

	private AudioFile		file;

	private Thumbnail		thumbnail;

	protected Song(int id, String name, String genre) {
		this.id = id;
		this.name = name;
		this.genre = genre;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Album getAlbum() {
		if (album == null) {
			return MediaDatabase.getAlbum(this);
		}
		return album;
	}

	public Interpreter getInterpreter() {
		if (interpreter == null) {
			return MediaDatabase.getInterpreter(this);
		}
		return interpreter;
	}

	public String getGenre() {
		return genre;
	}

	public AudioFile getFile() {
		if (file == null) {
			return MediaDatabase.getAudioFile(this);
		}
		return file;
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