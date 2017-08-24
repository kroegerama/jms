package de.janhoelscher.jms.database.media.scan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;

import javax.imageio.ImageIO;

import de.janhoelscher.jms.database.media.ImageFile;
import de.janhoelscher.jms.database.media.Library;
import de.janhoelscher.jms.database.media.MediaFile;
import de.janhoelscher.jms.database.media.scan.ffmpeg.AudioInfoExtractor;
import de.janhoelscher.jms.database.media.scan.ffmpeg.VideoInfoExtractor;
import de.janhoelscher.jms.logging.Logger;

public class MediaScanner {

	private Library		library;

	private FileFilter	fileFilter;

	public MediaScanner(Library library) {
		this.library = library;
		switch (library.getType()) {
			case SERIES:
			case MOVIES:
				fileFilter = VideoFileFilter.getInstance();
				break;
			case MUSIC:
				fileFilter = MusicFileFilter.getInstance();
				break;
			case PICTURES:
				fileFilter = ImageFileFilter.getInstance();
				break;
		}
	}

	public void scan() {
		scanDir(new File(library.getRootDirectory()));
	}

	private void scanDir(File directory) {
		for (File f : directory.listFiles(fileFilter)) {
			if (f.isDirectory()) {
				scanDir(f);
			} else {
				scanSingleFile(f);
			}
		}
	}

	private void scanSingleFile(File file) {
		MediaFile mFile = null;
		try {
			switch (library.getType()) {
				case SERIES:
				case MOVIES:
					mFile = VideoInfoExtractor.getVideoFileInformation(file);
					break;
				case MUSIC:
					mFile = AudioInfoExtractor.getAudioFileInformation(file);
				case PICTURES:
					mFile = getImageInformation(file);
				default:
					break;
			}
		} catch (IOException e) {
			Logger.warn("Failed to scan file \"" + file.getAbsolutePath() + "\"", e);
			//LogFactory.getLog(MediaScanner.class).warn("Failed to scan file \"" + file.getAbsolutePath() + "\"", e);
			return;
		}

		if (mFile != null) {
			Iterator<MediaInformationFinder> mInfoFinders = ServiceLoader.load(MediaInformationFinder.class).iterator();
			boolean found = false;
			while (mInfoFinders.hasNext() && !found) {
				found = mInfoFinders.next().findMediaInformation(library.getType(), mFile);
			}
			if (!found) {

			}
		}
	}

	private ImageFile getImageInformation(File imageFile) {
		try {
			BufferedImage image = ImageIO.read(imageFile);
			return new ImageFile(-1, imageFile.getAbsolutePath(), imageFile.length(), image.getWidth(), image.getHeight());
		} catch (IOException e) {
			Logger.warn("Failed to load ImageFile \"" + imageFile + "\"", e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to load ImageFile \"" + imageFile + "\"", e);
			return null;
		}
	}
}