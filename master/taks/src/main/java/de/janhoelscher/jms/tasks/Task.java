package de.janhoelscher.jms.tasks;

public class Task<T> {

	private final Runnable				runnable;

	private final TaskInformation<T>	taskInformation;

	private Thread						runThread;

	boolean								running;

	boolean								finished;

	public Task(Runnable runnable, TaskInformation<T> taskInformation) {
		this.runnable = runnable;
		this.taskInformation = taskInformation;
	}

	public synchronized void run() {
		if (running) {
			throw new IllegalStateException("Task is already running!");
		}
		finished = false;
		running = true;
		try {
			runnable.run();
		} finally {
			running = false;
			finished = true;
		}
	}

	public void runAsync() {
		if (running) {
			throw new IllegalStateException("Task is already running!");
		}
		runThread = new Thread(() -> {
			run();
			runThread = null;
		});
		runThread.start();
	}

	public TaskInformation<T> getTaskInformation() {
		return taskInformation;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isFinished() {
		return finished;
	}

	public void waitFor() throws InterruptedException {
		if (finished) {
			return;
		}
		if (runThread != null) {
			runThread.join();
		}
		while (!finished) {
			Thread.sleep(10);
		}
	}

	@Override
	public String toString() {
		return "Task [taskInformation=" + taskInformation + ", running=" + running + ", finished=" + finished + "]";
	}

}