package de.janhoelscher.jms.database.media.scan.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import de.janhoelscher.jms.config.Config;
import de.janhoelscher.jms.database.media.AudioFile;
import de.janhoelscher.jms.database.media.Library;
import de.janhoelscher.jms.database.media.MediaDatabase;
import de.janhoelscher.jms.database.media.VideoFile;
import de.janhoelscher.jms.tasks.DummyTask;
import de.janhoelscher.jms.tasks.Task;
import de.janhoelscher.jms.tasks.TaskInformation;

public class AudioStreamExtractor {

	private static final HashMap<String, Task<AudioFile>> extractAudioTasks = new HashMap<>();

	public static Task<AudioFile> extractAudio(VideoFile videoFile) throws IOException {
		Task<AudioFile> task = AudioStreamExtractor.extractAudioTasks.get(videoFile.getPath());
		if (task != null) {
			return task;
		}
		File rawFile;
		String videoFileName = FilenameUtils.getBaseName(videoFile.getPath());
		if (Config.getInstance().MediaLibrary.FFmpeg.StoreExtractedAudioInSameFolderAsVideo) {
			rawFile =
					new File(videoFile.getPath() + "." + Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioFileExtension);
		} else {
			rawFile =
					new File(Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioLocation + "/" + videoFileName + "." + Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioFileExtension);
		}
		if (!rawFile.getParentFile().exists() || !rawFile.getParentFile().isDirectory()) {
			rawFile.getParentFile().mkdirs();
		}
		AudioFile audioFile =
							MediaDatabase.createAudioFile(Library.NO_LIBRARY, rawFile.getAbsolutePath(), rawFile.length(), videoFile.getDuration(), 0);
		TaskInformation<AudioFile> info =
										new TaskInformation<>("AudioExtractionTask", "Extracting audio from: " + videoFileName, audioFile);
		if (rawFile.exists()) {
			DummyTask<AudioFile> dummy = new DummyTask<>(info);
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