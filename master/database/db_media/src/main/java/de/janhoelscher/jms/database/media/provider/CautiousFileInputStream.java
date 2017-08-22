package de.janhoelscher.jms.database.media.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.janhoelscher.jms.tasks.Task;

/**
 * FileInputStream-Wrapper to be used with files which are currently being modified (by the given task) <b>Warning:</b> Use only with small
 * (audio) files
 */
class CautiousFileInputStream extends InputStream {

	private static final int		caution	= 1024;

	private final Task<File>		task;

	private final FileInputStream	wrappedStream;

	public CautiousFileInputStream(Task<File> task) throws FileNotFoundException {
		this.task = task;
		this.wrappedStream = new FileInputStream(task.getTaskInformation().getAdditionalInformation());
	}

	@Override
	public int read() throws IOException {
		if (!task.isFinished()) {
			while (wrappedStream.available() < CautiousFileInputStream.caution) {
				sleep(10);
			}
		}
		return wrappedStream.read();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignored
		}
	}
}