package de.janhoelscher.jms.database.media.scan;

import de.janhoelscher.jms.database.media.Library.LibraryType;
import de.janhoelscher.jms.database.media.MediaFile;

public interface MediaInformationFinder {

	public boolean findMediaInformation(LibraryType type, MediaFile media);
}