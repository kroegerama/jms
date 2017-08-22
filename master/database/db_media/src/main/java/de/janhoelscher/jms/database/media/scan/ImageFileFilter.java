package de.janhoelscher.jms.database.media.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ImageFileFilter implements FileFilter {

	private static ImageFileFilter instance;

	public static ImageFileFilter getInstance() {
		if (ImageFileFilter.instance == null) {
			ImageFileFilter.instance = new ImageFileFilter();
		}
		return ImageFileFilter.instance;
	}

	private static Predicate<String> filenameFilter;

	private ImageFileFilter() {

	}

	private Predicate<String> getFilenameFilter() {
		if (ImageFileFilter.filenameFilter == null) {
			ImageFileFilter.filenameFilter = Pattern.compile("(\\.bmp|\\.gif|\\.ico|\\.img|\\.jpg|\\.png|\\.tif|\\.tiff|\\.webp|\\.svg)$").asPredicate();
		}
		return ImageFileFilter.filenameFilter;
	}

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || getFilenameFilter().test(file.getName());
	}
}
