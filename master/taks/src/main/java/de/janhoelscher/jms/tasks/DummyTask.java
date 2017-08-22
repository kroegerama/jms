package de.janhoelscher.jms.tasks;

public class DummyTask<T> extends Task<T> {

	public DummyTask(TaskInformation<T> taskInformation) {
		super(null, taskInformation);
	}

	@Override
	public synchronized void run() {

	}

	@Override
	public void runAsync() {

	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}