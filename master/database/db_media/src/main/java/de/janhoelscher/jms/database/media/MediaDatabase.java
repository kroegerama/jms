package de.janhoelscher.jms.database.media;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.media.Library.LibraryType;
import de.janhoelscher.jms.logging.Logger;

public abstract class MediaDatabase {

	private static final String	GET_VIDEO_BY_ID				= "SELECT * FROM videos WHERE id=?;";

	private static final String	GET_VIDEO_BY_PATH			= "SELECT * FROM videos WHERE path=?;";

	private static final String	CREATE_VIDEO_FILE			=
													"INSERT INTO videos (file,duration,size,width,height,framerate,extractedAudioLocation) VALUES(?,?,?,?,?,?,?);";

	private static final String	UPDATE_EXTRACTED_AUDIO_FILE	= "UPDATE videos SET extractedAudioFile=? WHERE id=?";

	private static final String	GET_LIBRARY_BY_ID			= "SELECT * FROM libraries WHERE id=?;";

	private static final String	UPDATE_LIBRARY_NAME			= "UPDATE libraries SET name=? WHERE id=?;";

	private static final String	UPDATE_LIBRARY_DIR			= "UPDATE libraries SET dir=? WHERE id=?;";

	private static final String	UPDATE_LIBRARY_TYPE			= "UPDATE libraries SET type=? WHERE id=?;";

	public static VideoFile getVideoFile(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_VIDEO_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToVideoFile(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load videofile with ID " + id, e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to load videofile with ID " + id, e);
			return null;
		}
	}

	public static VideoFile getVideoFile(String path) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_VIDEO_BY_PATH);
			stmt.setString(1, path);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToVideoFile(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load videofile with Path \"" + path + "\"", e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to load videofile with Path \"" + path + "\"", e);
			return null;
		}
	}

	public static VideoFile createVideoFile(String file, long duration, long size, int width, int height, float framerate, String extractedAudioFile) {

		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_VIDEO_FILE);
			stmt.setString(1, file);
			stmt.setLong(2, duration);
			stmt.setLong(3, size);
			stmt.setInt(4, width);
			stmt.setInt(5, height);
			stmt.setFloat(6, framerate);
			stmt.setString(7, extractedAudioFile);
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed to create videofile with Path \"" + file + "\"", e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to create videofile with Path \"" + file + "\"", e);
			return null;
		}
		return MediaDatabase.getVideoFile(file);
	}

	public static void updateExtractedAudioFile(VideoFile videoFile) {

		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_EXTRACTED_AUDIO_FILE);
			stmt.setInt(2, videoFile.getId());
			stmt.setString(1, videoFile.getExtractedAudioFile());
			stmt.execute();
		} catch (SQLException e) {
			Logger.warn("Failed to update extractedAudioPath for videofile with ID " + videoFile.getId(), e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to update extractedAudioPath for videofile with ID " + videoFile.getId(), e);
		}
	}

	private static VideoFile resultSetToVideoFile(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String path = rs.getString("file");
		long duration = rs.getLong("duration");
		long size = rs.getLong("size");
		int width = rs.getInt("width");
		int height = rs.getInt("height");
		float framerate = rs.getFloat("framerate");
		String extractedAudioFile = rs.getString("extractedAudioFile");
		return new VideoFile(id, path, duration, size, width, height, framerate, extractedAudioFile);
	}

	public static Library getLibrary(int id) {

		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_LIBRARY_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToLibrary(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load library with ID " + id, e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to load library with ID " + id, e);
			return null;
		}
	}

	private static Library resultSetToLibrary(ResultSet rs) throws SQLException {

		int id = rs.getInt("id");
		String name = rs.getString("name");
		String rootDir = rs.getString("dir");
		LibraryType type = LibraryType.valueOf(rs.getInt("type"));
		return new Library(id, name, rootDir, type);
	}

	public static void updateName(Library library) {

		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_LIBRARY_NAME);
			stmt.setString(1, library.getName());
			stmt.setInt(2, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			Logger.warn("Failed to update name for library with ID " + library.getId(), e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to update name for library with ID " + library.getId(), e);
		}
	}

	public static void updateRootDirectory(Library library) {

		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_LIBRARY_DIR);
			stmt.setString(1, library.getRootDirectory());
			stmt.setInt(2, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			Logger.warn("Failed to update dir for library with ID " + library.getId(), e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to update dir for library with ID " + library.getId(), e);
		}
	}

	public static void updateType(Library library) {

		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_LIBRARY_TYPE);
			stmt.setInt(1, library.getType().DB_ID);
			stmt.setInt(2, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			Logger.warn("Failed to update type for library with ID " + library.getId(), e);
			//LogFactory.getLog(MediaDatabase.class).warn("Failed to update type for library with ID " + library.getId(), e);
		}
	}
}