package de.janhoelscher.jms.setup;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import de.janhoelscher.jms.config.Config;
import de.janhoelscher.jms.database.Database;
import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.media.scan.ffmpeg.FFmpeg;
import de.janhoelscher.jms.logging.Logger;

public abstract class Setup {

	private static final String	LOCKFILE	= ".setup.lock";

	private static boolean		complete;

	public static boolean isComplete() {
		if (!Setup.complete) {
			File setupFile = new File(Setup.LOCKFILE);
			Setup.complete = setupFile.exists();
		}
		return Setup.complete;
	}

	public static void run() {
		if (Setup.isComplete()) {
			return;
		}
		Setup.runConfigSetup();
		Setup.runDatabaseSetup();
		try {
			new File(Setup.LOCKFILE).createNewFile();
		} catch (IOException e) {

		}
	}

	private static void runConfigSetup() {
		Scanner scan = new Scanner(System.in);
		Config cfg = Config.getInstance();
		File ffmpegFile = new File(cfg.MediaLibrary.FFmpeg.Path + "/" + FFmpeg.FFMPEG);
		while (!ffmpegFile.exists()) {
			String ffmpegPath = Setup.getStringAnswer(scan, "Please enter the path to FFmpeg.", "If you do not have FFmpeg installed, you can download it here: https://www.ffmpeg.org/download.html");
			cfg.MediaLibrary.FFmpeg.Path = ffmpegPath;
			ffmpegFile = new File(ffmpegPath + "/" + FFmpeg.FFMPEG);
		}
		boolean sameFolder = Setup.getYesNoAnswer(scan, "JMS may need to extract some audio streams from your video files in order to play them properly in your browser.", "Do you want JMS to store the audio in the same folder as the video?");
		cfg.MediaLibrary.FFmpeg.StoreExtractedAudioInSameFolderAsVideo = sameFolder;
		if (!sameFolder) {
			String extractedFolder = Setup.getStringAnswer(scan, "Please specify the directory in which the audio files should be stored.");
			cfg.MediaLibrary.FFmpeg.ExtractedAudioLocation = extractedFolder;
			new File(extractedFolder).mkdirs();
		}
		boolean changeDefaultExtension;
		if (cfg.MediaLibrary.FFmpeg.ExtractedAudioFileExtension == null || cfg.MediaLibrary.FFmpeg.ExtractedAudioFileExtension.isEmpty()) {
			changeDefaultExtension = true;
			System.out.println("> You need to specify a default extension for extracted audio files.");
		} else {
			changeDefaultExtension = !Setup.getYesNoAnswer(scan, "Do you want to keep the default file-extension (." + cfg.MediaLibrary.FFmpeg.ExtractedAudioFileExtension + ") for extracted audio files?");
		}
		if (changeDefaultExtension) {
			String newExtension = Setup.getStringAnswer(scan, "Please specify the extension to use.");
			if (newExtension.startsWith(".")) {
				newExtension = newExtension.substring(1);
			}
			cfg.MediaLibrary.FFmpeg.ExtractedAudioFileExtension = newExtension;
		}
		cfg.MediaLibrary.AutoConvertLargeFileTypes = Setup.getYesNoAnswer(scan, "Should large video-files automatically be converted to a smaller format for streaming?");
		Config.save();
	}

	private static boolean getYesNoAnswer(Scanner scan, String... question) {
		for (int i = 0; i < question.length - 1; i++) {
			System.out.println("> " + question[i]);
		}
		System.out.println("> " + question[question.length - 1] + " (Y/N)");
		System.out.print("> ");
		String answer = scan.nextLine().trim();
		return answer.equalsIgnoreCase("y");
	}

	private static String getStringAnswer(Scanner scan, String... question) {
		for (String element : question) {
			System.out.println("> " + element);
		}
		System.out.print("> ");
		return scan.nextLine().trim();
	}

	private static void runDatabaseSetup() {
		Database db = DatabaseFactory.getDatabase();
		try {
			String media_structure = IOUtils.toString(new InputStreamReader(Setup.class.getResourceAsStream("/de/janhoelscher/jms/db_structure/media.sql")));
			db.executeMultiple(media_structure);
		} catch (Exception e) {
			Logger.warn("Failed to create media databse structure!", e);
		}
		try {
			String users_structure = IOUtils.toString(new InputStreamReader(Setup.class.getResourceAsStream("/de/janhoelscher/jms/db_structure/users.sql")));
			db.executeMultiple(users_structure);
		} catch (Exception e) {
			Logger.warn("Failed to create user databse structure!", e);
		}
	}
}