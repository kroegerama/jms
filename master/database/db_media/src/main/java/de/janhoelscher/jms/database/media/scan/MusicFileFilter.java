package de.janhoelscher.jms.database.media.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MusicFileFilter implements FileFilter {

	private static MusicFileFilter instance;

	public static MusicFileFilter getInstance() {
		if (MusicFileFilter.instance == null) {
			MusicFileFilter.instance = new MusicFileFilter();
		}
		return MusicFileFilter.instance;
	}

	private static Predicate<String> filenameFilter;

	private MusicFileFilter() {

	}

	private Predicate<String> getFilenameFilter() {
		if (MusicFileFilter.filenameFilter == null) {
			MusicFileFilter.filenameFilter = Pattern.compile("(\\.aa|\\.aac|\\.aax|\\.aiff|\\.flac|\\.m4a|\\.m4p|\\.mp3|\\.ogg|\\.vox|\\.wav|\\.wma|\\.webm)$").asPredicate();
		}
		return MusicFileFilter.filenameFilter;
	}

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || getFilenameFilter().test(file.getName());
	}
}
