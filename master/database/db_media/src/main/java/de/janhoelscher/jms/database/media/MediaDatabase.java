package de.janhoelscher.jms.database.media;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.LogFactory;

import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.media.Library.LibraryType;

public abstract class MediaDatabase {

	private static final String	GET_VIDEO_BY_ID		= "SELECT * FROM videos WHERE id=?;";

	private static final String	GET_VIDEO_BY_PATH	= "SELECT * FROM videos WHERE path=?;";

	private static final String	CREATE_VIDEO_FILE	=
													"INSERT INTO videos (path,duration,size,width,height,framerate,extractedAudioLocation) VALUES(?,?,?,?,?,?,?);";

	private static final String	GET_LIBRARY_BY_ID	= "SELECT * FROM libraries WHERE id=?;";

	private static final String	UPDATE_LIBRARY_NAME	= "UPDATE libraries SET name=? WHERE id=?;";

	private static final String	UPDATE_LIBRARY_DIR	= "UPDATE libraries SET dir=? WHERE id=?;";

	private static final String	UPDATE_LIBRARY_TYPE	= "UPDATE libraries SET type=? WHERE id=?;";

	public static VideoFile getVideoFile(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_VIDEO_BY_ID);
			stmt.setInt(0, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToVideoFile(rs);
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to load videofile with ID " + id, e);
			return null;
		}
	}

	public static VideoFile getVideoFile(String path) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_VIDEO_BY_PATH);
			stmt.setString(0, path);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToVideoFile(rs);
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to load videofile with Path \"" + path + "\"", e);
			return null;
		}
	}

	public static VideoFile createVideoFile(String file, long duration, long size, int width, int height, float framerate, String extractedAudioFile) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_VIDEO_FILE);
			stmt.setString(0, file);
			stmt.setLong(1, duration);
			stmt.setLong(2, size);
			stmt.setInt(3, width);
			stmt.setInt(4, height);
			stmt.setFloat(5, framerate);
			stmt.setString(6, extractedAudioFile);
			stmt.executeUpdate();
			return MediaDatabase.getVideoFile(file);
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to create videofile with Path \"" + file + "\"", e);
			return null;
		}
	}

	private static VideoFile resultSetToVideoFile(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String path = rs.getString("path");
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
			stmt.setInt(0, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToLibrary(rs);
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to load library with ID " + id, e);
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
			stmt.setString(0, library.getName());
			stmt.setInt(1, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to update name for library with ID " + library.getId(), e);
		}
	}

	public static void updateRootDirectory(Library library) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_LIBRARY_DIR);
			stmt.setString(0, library.getRootDirectory());
			stmt.setInt(1, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to update dir for library with ID " + library.getId(), e);
		}
	}

	public static void updateType(Library library) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_LIBRARY_TYPE);
			stmt.setInt(0, library.getType().DB_ID);
			stmt.setInt(1, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			LogFactory.getLog(MediaDatabase.class).warn("Failed to update type for library with ID " + library.getId(), e);
		}
	}
}