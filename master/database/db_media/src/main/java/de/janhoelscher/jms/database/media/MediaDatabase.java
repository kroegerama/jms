package de.janhoelscher.jms.database.media;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.media.Episode.EpisodeWatchState;
import de.janhoelscher.jms.database.media.Library.LibraryType;
import de.janhoelscher.jms.database.media.Movie.MovieWatchState;
import de.janhoelscher.jms.database.users.User;
import de.janhoelscher.jms.logging.Logger;

public abstract class MediaDatabase {

	// RAW FILE MANAGEMENT
	private static final String	CREATE_LIBRARY				=
												"INSERT INTO libraries (name,librarytype,rootDir) VALUES(?,?,?);";

	private static final String	CREATE_FILE					= "INSERT INTO files (libraryid,path,size) VALUES(?,?,?);";

	private static final String	CREATE_AUDIO				=
												"INSERT INTO audios (fileid,duration,bitrate) VALUES(?,?,?);";

	private static final String	CREATE_IMAGE				= "INSERT INTO images (fileid,width,height) VALUES(?,?,?);";

	private static final String	CREATE_VIDEO				=
												"INSERT INTO videos (fileid,duration,width,height,framerate) VALUES(?,?,?,?,?)";

	private static final String	GET_LIBRARY_BY_ID			= "SELECT * FROM libraries WHERE id=?;";

	private static final String	UPDATE_LIBRARY_NAME			= "UPDATE libraries SET name=? WHERE id=?;";

	private static final String	GET_FILE_ID_BY_PATH			= "SELECT id FROM files WHERE path=?;";

	private static final String	GET_AUDIO_BY_ID				=
												"SELECT * FROM audios INNER JOIN files ON audios.fileid = files.id WHERE audios.fileid=?;";

	private static final String	GET_IMAGE_BY_ID				=
												"SELECT * FROM images INNER JOIN files ON images.fileid = files.id WHERE images.fileid=?;";

	private static final String	GET_VIDEO_BY_ID				=
												"SELECT * FROM videos INNER JOIN files ON videos.fileid = files.id WHERE videos.fileid=?;";

	private static final String	GET_EXTRACTED_AUDIO_FILE	=
															"SELECT * FROM (videos INNER JOIN (audios INNER JOIN files ON audios.fileid = files.id) ON videos.extractedAudioFile = audios.fileid) WHERE videos.fileid=?";

	private static final String	UPDATE_EXTRACTED_AUDIO_FILE	= "UPDATE videos SET extractedAudioFile=? WHERE fileid=?";

	public static Library getLibrary(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_LIBRARY_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			id = rs.getInt("id");
			String name = rs.getString("name");
			LibraryType type = LibraryType.valueOf(rs.getInt("librarytype"));
			String rootDir = rs.getString("rootDir");
			return new Library(id, name, type, rootDir);
		} catch (SQLException e) {
			Logger.warn("Failed to load library with ID " + id, e);
			return null;
		}
	}

	public static int getFileId(String absolutePath) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_FILE_ID_BY_PATH);
			stmt.setString(1, absolutePath);
			ResultSet rs = stmt.executeQuery();
			return rs.getInt("id");
		} catch (SQLException e) {
			Logger.warn("Failed to get fileid for path " + absolutePath, e);
			return -1;
		}
	}

	public static AudioFile getAudioFile(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_AUDIO_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToAudioFile(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load videofile with ID " + id, e);
			return null;
		}
	}

	public static ImageFile getImageFile(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_IMAGE_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToImageFile(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load videofile with ID " + id, e);
			return null;
		}
	}

	public static VideoFile getVideoFile(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_VIDEO_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToVideoFile(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load videofile with ID " + id, e);
			return null;
		}
	}

	public static Library createLibrary(String name, LibraryType type, String rootDir) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_LIBRARY);
			stmt.setString(1, name);
			stmt.setInt(2, type.RAW_TYPE);
			stmt.setString(3, rootDir);
			return MediaDatabase.getLibrary(stmt.getGeneratedKeys().getInt("id"));
		} catch (SQLException e) {
			Logger.warn("Failed to create library \"" + name + "\"", e);
			return null;
		}
	}

	public static int createFile(Library library, String path, long size) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_FILE);
			stmt.setInt(1, library.getId());
			stmt.setString(2, path);
			stmt.setLong(3, size);
			return stmt.getGeneratedKeys().getInt("id");
		} catch (SQLException e) {
			Logger.warn("Failed to create file with Path \"" + path + "\"", e);
			return -1;
		}
	}

	public static AudioFile createAudioFile(Library library, String path, long size, long duration, int bitrate) {
		try {
			int fileId = MediaDatabase.createFile(library, path, size);
			if (fileId <= 0) {
				return null;
			}
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_AUDIO);
			stmt.setInt(1, fileId);
			stmt.setLong(2, duration);
			stmt.setInt(3, bitrate);
			stmt.executeUpdate();
			return MediaDatabase.getAudioFile(fileId);
		} catch (SQLException e) {
			Logger.warn("Failed to create audiofile with Path \"" + path + "\"", e);
			return null;
		}
	}

	public static ImageFile createImageFile(Library library, String path, long size, int width, int height) {
		try {
			int fileId = MediaDatabase.createFile(library, path, size);
			if (fileId <= 0) {
				return null;
			}
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_IMAGE);
			stmt.setInt(1, fileId);
			stmt.setInt(2, width);
			stmt.setInt(3, height);
			stmt.executeUpdate();
			return MediaDatabase.getImageFile(fileId);
		} catch (SQLException e) {
			Logger.warn("Failed to create imagefile with Path \"" + path + "\"", e);
			return null;
		}
	}

	public static VideoFile createVideoFile(Library library, String path, long size, long duration, int width, int height, float framerate) {
		try {
			int fileId = MediaDatabase.createFile(library, path, size);
			if (fileId <= 0) {
				return null;
			}
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.CREATE_VIDEO);
			stmt.setInt(1, fileId);
			stmt.setLong(2, duration);
			stmt.setInt(3, width);
			stmt.setInt(4, height);
			stmt.setFloat(5, framerate);
			stmt.executeUpdate();
			return MediaDatabase.getVideoFile(fileId);
		} catch (SQLException e) {
			Logger.warn("Failed to create videofile with Path \"" + path + "\"", e);
			return null;
		}
	}

	public static AudioFile getExtractedAudioFile(VideoFile videoFile) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.GET_EXTRACTED_AUDIO_FILE);
			stmt.setInt(1, videoFile.getId());
			ResultSet rs = stmt.executeQuery();
			return MediaDatabase.resultSetToAudioFile(rs);
		} catch (SQLException e) {
			Logger.warn("Failed to load extracted audio for video " + videoFile, e);
			return null;
		}
	}

	public static void updateExtractedAudioFile(VideoFile videoFile) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_EXTRACTED_AUDIO_FILE);
			stmt.setInt(1, videoFile.getExtractedAudioFile().getId());
			stmt.setInt(2, videoFile.getId());
			stmt.execute();
		} catch (SQLException e) {
			Logger.warn("Failed to update extractedAudioPath for videofile with ID " + videoFile.getId(), e);
		}
	}

	public static void updateName(Library library) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(MediaDatabase.UPDATE_LIBRARY_NAME);
			stmt.setString(1, library.getName());
			stmt.setInt(2, library.getId());
			stmt.execute();
		} catch (SQLException e) {
			Logger.warn("Failed to update name for library with ID " + library.getId(), e);
		}
	}

	private static AudioFile resultSetToAudioFile(ResultSet rs) throws SQLException {
		int id = rs.getInt("files.id");
		int libraryid = rs.getInt("files.libraryid");
		String path = rs.getString("files.path");
		long size = rs.getLong("files.size");
		long duration = rs.getLong("audios.duration");
		int bitrate = rs.getInt("audios.bitrate");
		return new AudioFile(id, libraryid, path, size, duration, bitrate);
	}

	private static ImageFile resultSetToImageFile(ResultSet rs) throws SQLException {
		int id = rs.getInt("files.id");
		int libraryid = rs.getInt("files.libraryid");
		String path = rs.getString("files.path");
		long size = rs.getLong("files.size");
		int width = rs.getInt("images.duration");
		int height = rs.getInt("images.bitrate");
		return new ImageFile(id, libraryid, path, size, width, height);
	}

	private static VideoFile resultSetToVideoFile(ResultSet rs) throws SQLException {
		int id = rs.getInt("files.id");
		int libraryid = rs.getInt("files.libraryid");
		String path = rs.getString("files.path");
		long size = rs.getLong("files.size");
		long duration = rs.getLong("videos.duration");
		int width = rs.getInt("videos.width");
		int height = rs.getInt("videos.height");
		float framerate = rs.getFloat("videos.framerate");
		return new VideoFile(id, libraryid, path, size, duration, width, height, framerate);
	}

	public static void updateThumbnailImageData(Thumbnail thumbnail) {
		// TODO Auto-generated method stub

	}

	public static void updateThumbnailSource(Thumbnail thumbnail) {
		// TODO Auto-generated method stub

	}

	public static Interpreter getInterpreter(Song song) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Album getAlbum(Song song) {
		// TODO Auto-generated method stub
		return null;
	}

	public static AudioFile getAudioFile(Song song) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Thumbnail getThumbnail(Interpreter interpreter) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateThumbnail(Interpreter interpreter) {
		// TODO Auto-generated method stub

	}

	public static Interpreter getInterpreter(Album album) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Thumbnail getThumbnail(Album album) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Thumbnail getThumbnail(Song song) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Publisher getPublisher(Movie movie) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Thumbnail getThumbnail(Movie movie) {
		// TODO Auto-generated method stub
		return null;
	}

	public static VideoFile getVideoFile(Movie movie) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Thumbnail getThumbnail(Series series) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateThumbnail(Series series) {
		// TODO Auto-generated method stub

	}

	public static Series getSeries(Season season) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void getEpisodes(Season season) {
		// TODO Auto-generated method stub

	}

	public static Season getSeason(Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Series getSeries(Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void getSeasons(Series series) {
		// TODO Auto-generated method stub

	}

	public static MovieWatchState getWatchState(User user, Movie movie) {
		// TODO Auto-generated method stub
		return null;
	}

	public static EpisodeWatchState getWatchState(User user, Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateThumbnail(Album album) {
		// TODO Auto-generated method stub

	}

	public static Thumbnail getThumbnail(Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateThumbnail(Episode episode) {
		// TODO Auto-generated method stub

	}

	public static void updateThumbnail(Song song) {
		// TODO Auto-generated method stub

	}
}