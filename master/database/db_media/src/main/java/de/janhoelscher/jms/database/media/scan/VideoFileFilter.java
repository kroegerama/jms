package de.janhoelscher.jms.database.media.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class VideoFileFilter implements FileFilter {

	private static VideoFileFilter instance;

	public static VideoFileFilter getInstance() {
		if (VideoFileFilter.instance == null) {
			VideoFileFilter.instance = new VideoFileFilter();
		}
		return VideoFileFilter.instance;
	}

	private static Predicate<String> filenameFilter;

	private VideoFileFilter() {

	}

	private Predicate<String> getFilenameFilter() {
		if (VideoFileFilter.filenameFilter == null) {
			VideoFileFilter.filenameFilter = Pattern.compile("(\\.mkv|\\.mp4|\\.m4p|\\.m4v|\\.flv|\\.webm|\\.vob|\\.avi|\\.wmv|\\.3gp)$")
													.asPredicate();
		}
		return VideoFileFilter.filenameFilter;
	}

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || getFilenameFilter().test(file.getName());
	}
}