package de.janhoelscher.jms.database.media.scan.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.janhoelscher.jms.config.Config;
import de.janhoelscher.jms.tasks.DummyTask;
import de.janhoelscher.jms.tasks.Task;
import de.janhoelscher.jms.tasks.TaskInformation;

public class AudioStreamExtractor {

	private static final HashMap<String, Task<File>> extractAudioTasks = new HashMap<>();

	public static Task<File> extractAudio(String path) throws IOException {
		return AudioStreamExtractor.extractAudio(new File(path));
	}

	public static Task<File> extractAudio(File videoFile) throws IOException {
		Task<File> task = AudioStreamExtractor.extractAudioTasks.get(videoFile.getAbsolutePath());
		if (task != null) {
			return task;
		}
		File audioFile;
		if (Config.getInstance().MediaLibrary.FFmpeg.StoreExtractedAudioInSameFolderAsVideo) {
			audioFile = new File(videoFile.getParentFile().getPath() + "/" + videoFile.getName() + "." + Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioFileExtension);
		} else {
			audioFile = new File(Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioLocation + "/" + videoFile.getName() + "." + Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioFileExtension);
		}
		if (!audioFile.getParentFile().exists() || !audioFile.getParentFile().isDirectory()) {
			audioFile.getParentFile().mkdirs();
		}
		TaskInformation<File> info = new TaskInformation<>("AudioExtractionTask", "Extracting audio from: " + videoFile.getName(), audioFile);
		if (audioFile.exists()) {
			DummyTask<File> dummy = new DummyTask<>(info);
			dummy.setFinished(true);
			dummy.setRunning(false);
			return dummy;
		}

		Runnable preparedRun = FFmpeg.prepareRunFFmpeg("-i \"" + videoFile.getPath() + "\" -vn -acodec " + Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioCodec + " \"" + audioFile.getPath() + "\"");
		task = new Task<>(preparedRun, info);
		task.runAsync();
		return task;
	}
}