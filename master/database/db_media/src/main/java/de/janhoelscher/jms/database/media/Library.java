package de.janhoelscher.jms.database.media;

public class Library {

	public static final Library	NO_LIBRARY	= new Library(-1, null, null, null);

	private final int			id;

	private String				name;

	private final LibraryType	type;

	private final String		rootDir;

	public Library(int id, String name, LibraryType type, String rootDirectory) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.rootDir = rootDirectory;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
			MediaDatabase.updateName(this);
		}
	}

	public String getRootDir() {
		return rootDir;
	}

	public LibraryType getType() {
		return type;
	}

	public static enum LibraryType {
									MOVIES(0),
									SERIES(1),
									MUSIC(2),
									PICTURES(3);

		public final int RAW_TYPE;

		private LibraryType(int dbId) {
			this.RAW_TYPE = dbId;
		}

		public static LibraryType valueOf(int id) {
			switch (id) {
				case 0:
					return MOVIES;
				case 1:
					return SERIES;
				case 2:
					return MUSIC;
				case 3:
					return PICTURES;
			}
			return null;
		}
	}
}