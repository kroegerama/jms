package de.janhoelscher.jms.database.media.scan.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import de.janhoelscher.jms.config.Config;
import de.janhoelscher.jms.logging.Logger;

public class FFmpeg {

	public static final String FFMPEG = "ffmpeg.exe";

	static Runnable prepareRunFFmpeg(String cmdLineArgs) {
		return () -> {
			try {
				ProcessBuilder builder = new ProcessBuilder("cmd", "/c", Config.getInstance().MediaLibrary.FFmpeg.Path + "/" + FFmpeg.FFMPEG + " -hide_banner " + cmdLineArgs);
				builder.redirectErrorStream(true);
				File file = File.createTempFile(UUID.randomUUID().toString(), null);
				builder.redirectOutput(file);
				builder.start().waitFor();
			} catch (Exception e) {
				Logger.warn("Failed to run program.", e);
				//LogFactory.getLog(FFmpeg.class).warn("Failed to run program.", e);
			}
		};
	}

	static InputStream runFFmpeg(String cmdLineArgs) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("cmd", "/c", Config.getInstance().MediaLibrary.FFmpeg.Path + "/" + FFmpeg.FFMPEG + " -hide_banner " + cmdLineArgs);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		return process.getInputStream();
	}
}