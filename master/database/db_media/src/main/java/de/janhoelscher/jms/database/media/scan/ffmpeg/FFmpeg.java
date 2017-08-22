package de.janhoelscher.jms.database.media.scan.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.logging.LogFactory;

import de.janhoelscher.jms.config.Config;
import de.janhoelscher.jms.database.media.VideoFile;
import de.janhoelscher.jms.tasks.DummyTask;
import de.janhoelscher.jms.tasks.Task;
import de.janhoelscher.jms.tasks.TaskInformation;

public class FFmpeg {

	public static final String							FFMPEG				= "ffmpeg.exe";

	public static final String							FFPROBE				= "ffprobe.exe";

	private static final HashMap<String, Task<File>>	extractAudioTasks	= new HashMap<>();

	public static VideoFile getVideoFileInformation(String path) throws IOException {
		return FFmpeg.getVideoFileInformation(new File(path));
	}

	public static VideoFile getVideoFileInformation(File file) throws IOException {
		InputStream in = FFmpeg.runProgram(FFmpeg.FFMPEG, "-hide_banner -i \"" + file.getPath() + "\"");
		Scanner scan = new Scanner(in);
		long duration = -1;
		int width = -1;
		int height = -1;
		float framerate = -1;
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.startsWith("Duration")) {
				duration = FFmpeg.getDuration(line);
			} else if (line.startsWith("Stream") && line.contains("Video")) {
				float[] res = FFmpeg.getResolutionAndFramerate(line);
				width = (int) res[0];
				height = (int) res[1];
				framerate = res[2];
			}
		}
		scan.close();
		return new VideoFile(-1, file.getAbsolutePath(), duration, file.length(), width, height, framerate, null);
	}

	private static long getDuration(String line) {
		if (!line.startsWith("Duration")) {
			return -1;
		}
		long h = Integer.parseInt(line.substring(10, 12));
		long m = Integer.parseInt(line.substring(13, 15));
		long s = Integer.parseInt(line.substring(16, 18));
		long ms = Integer.parseInt(line.substring(19, 21));

		return ((h * 60 + m) * 60 + s) * 1000 + ms;
	}

	private static float[] getResolutionAndFramerate(String line) {
		float[] res = new float[] { -1, -1, -1 };
		if (!line.startsWith("Stream")) {
			return res;
		}
		String[] parts = line.split("\\,");
		for (String s : parts) {
			if (s.contains("fps")) {
				res[2] = Float.parseFloat(s.substring(1, 6));
			} else if (s.contains("x")) {
				String[] nums = s.split("x");
				if (nums.length == 2) {
					try {
						res[0] = Integer.parseInt(nums[0].substring(1));
						res[1] = Integer.parseInt(nums[1]);
					} catch (Exception e) {
						// ignored
					}
				}
			}
		}
		return res;
	}

	public static Task<File> extractAudio(String path) throws IOException {
		return FFmpeg.extractAudio(new File(path));
	}

	public static Task<File> extractAudio(File videoFile) throws IOException {
		Task<File> task = FFmpeg.extractAudioTasks.get(videoFile.getAbsolutePath());
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

		Runnable preparedRun = FFmpeg.prepareRunProgram(FFmpeg.FFMPEG, "-hide_banner -i \"" + videoFile.getPath() + "\" -vn -acodec " + Config.getInstance().MediaLibrary.FFmpeg.ExtractedAudioCodec + " \"" + audioFile.getPath() + "\"");
		task = new Task<>(preparedRun, info);
		task.runAsync();
		return task;
	}

	private static Runnable prepareRunProgram(String program, String cmdLineArgs) {
		return () -> {
			try {
				ProcessBuilder builder = new ProcessBuilder("cmd", "/c", Config.getInstance().MediaLibrary.FFmpeg.Path + "/" + program + " " + cmdLineArgs);
				builder.redirectErrorStream(true);
				File file = File.createTempFile(UUID.randomUUID().toString(), null);
				builder.redirectOutput(file);
				builder.start().waitFor();
			} catch (Exception e) {
				LogFactory.getLog(FFmpeg.class).warn("Failed to run program.", e);
			}
		};
	}

	private static InputStream runProgram(String program, String cmdLineArgs) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("cmd", "/c", Config.getInstance().MediaLibrary.FFmpeg.Path + "/" + program + " " + cmdLineArgs);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		return process.getInputStream();
	}
}