package de.janhoelscher.jms.database.media.scan;

import de.janhoelscher.jms.database.media.Library.LibraryType;
import de.janhoelscher.jms.database.media.MediaFile;

class DefaultMediaInformationFinder implements MediaInformationFinder {

	@Override
	public boolean findMediaInformation(LibraryType type, MediaFile media) {
		return false;
	}

}