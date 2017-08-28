package de.janhoelscher.jms.database.media.scan.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import de.janhoelscher.jms.database.media.Library;
import de.janhoelscher.jms.database.media.MediaDatabase;
import de.janhoelscher.jms.database.media.VideoFile;

public class VideoInfoExtractor {

	public static VideoFile getVideoFileInformation(Library lib, String path) throws IOException {
		return VideoInfoExtractor.getVideoFileInformation(lib, new File(path));
	}

	public static VideoFile getVideoFileInformation(Library lib, File file) throws IOException {
		InputStream in = FFmpeg.runFFmpeg("-i \"" + file.getPath() + "\"");
		Scanner scan = new Scanner(in);
		long duration = -1;
		int width = -1;
		int height = -1;
		float framerate = -1;
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.startsWith("Duration")) {
				duration = VideoInfoExtractor.getDuration(line);
			} else if (line.startsWith("Stream") && line.contains("Video")) {
				float[] res = VideoInfoExtractor.getResolutionAndFramerate(line);
				width = (int) res[0];
				height = (int) res[1];
				framerate = res[2];
			}
		}
		scan.close();
		return MediaDatabase.createVideoFile(lib, file.getAbsolutePath(), file.length(), duration, width, height, framerate);
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
}