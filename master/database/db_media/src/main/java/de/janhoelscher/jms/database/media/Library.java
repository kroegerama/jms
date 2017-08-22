package de.janhoelscher.jms.database.media;

public class Library {

	private final int	id;

	private String		name;

	private String		rootDirectory;

	private LibraryType	type;

	public Library(int id, String name, String rootDirectory, LibraryType type) {
		this.id = id;
		this.name = name;
		this.rootDirectory = rootDirectory;
		this.type = type;
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

	public String getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(String rootDirectory) {
		if (!this.rootDirectory.equals(rootDirectory)) {
			this.rootDirectory = rootDirectory;
			MediaDatabase.updateRootDirectory(this);
		}
	}

	public LibraryType getType() {
		return type;
	}

	public void setType(LibraryType type) {
		if (this.type != type) {
			this.type = type;
			MediaDatabase.updateType(this);
		}
	}

	public static enum LibraryType {
									MOVIES(0),
									SERIES(1),
									MUSIC(2),
									PICTURES(3);

		public final int DB_ID;

		private LibraryType(int dbId) {
			this.DB_ID = dbId;
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