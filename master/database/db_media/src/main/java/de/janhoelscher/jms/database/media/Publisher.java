package de.janhoelscher.jms.database.media;

public class Publisher {

	private final String name;

	protected Publisher(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}