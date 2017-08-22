package de.janhoelscher.jms.tasks;

public class TaskInformation<T> {

	private final String	name;
	private String			description;
	private final T			additionalInformation;

	public TaskInformation(String name, T additionalInformation) {
		this.name = name;
		this.additionalInformation = additionalInformation;
	}

	public TaskInformation(String name, String description, T additionalInformation) {
		this.name = name;
		this.description = description;
		this.additionalInformation = additionalInformation;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public T getAdditionalInformation() {
		return additionalInformation;
	}

	@Override
	public String toString() {
		return "TaskInformation [name=" + name + ", description=\"" + description + "\", additionalInformation=" + additionalInformation + "]";
	}
}