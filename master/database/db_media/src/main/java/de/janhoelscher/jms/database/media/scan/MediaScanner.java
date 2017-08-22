package de.janhoelscher.jms.database.media.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.ServiceLoader;

import de.janhoelscher.jms.database.media.Library;

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
				scanFile(f);
			}
		}
	}

	private void scanFile(File file) {

		ServiceLoader.load(MediaInformationFinder.class).iterator();
	}
}