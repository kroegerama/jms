package de.janhoelscher.jms.database.media.scan.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import de.janhoelscher.jms.database.media.AudioFile;

public class AudioInfoExtractor {

	public static AudioFile getAudioFileInformation(String path) throws IOException {
		return AudioInfoExtractor.getAudioFileInformation(new File(path));
	}

	public static AudioFile getAudioFileInformation(File file) throws IOException {
		InputStream in = FFmpeg.runFFmpeg("-i \"" + file.getPath() + "\"");
		Scanner scan = new Scanner(in);
		long[] durationAndBitrate = new long[] { -1, -1 };
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.startsWith("Duration")) {
				durationAndBitrate = AudioInfoExtractor.getDurationAndBitrate(line);
				break;
			}
		}
		scan.close();
		return new AudioFile(-1, file.getAbsolutePath(), durationAndBitrate[0], file.length(), (int) durationAndBitrate[1]);
	}

	private static long[] getDurationAndBitrate(String line) {
		long[] res = new long[] { -1, -1 };
		if (!line.startsWith("Duration")) {
			return res;
		}

		long h = Integer.parseInt(line.substring(10, 12));
		long m = Integer.parseInt(line.substring(13, 15));
		long s = Integer.parseInt(line.substring(16, 18));
		long ms = Integer.parseInt(line.substring(19, 21));

		res[0] = ((h * 60 + m) * 60 + s) * 1000 + ms;

		int index = line.indexOf("bitrate");
		if (index > 0) {
			res[1] = Integer.parseInt(line.substring(index + 9, line.indexOf("kb/s")));

		}

		return res;
	}
}