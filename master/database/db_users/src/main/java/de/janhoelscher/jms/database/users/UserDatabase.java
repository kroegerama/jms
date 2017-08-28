package de.janhoelscher.jms.database.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;

import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.users.permissions.PermissionSet;
import de.janhoelscher.jms.logging.Logger;

public abstract class UserDatabase {

	// Users
	private static final String	GET_USER_BY_ID					= "SELECT id,name FROM users WHERE id=?;";

	private static final String	GET_USER_BY_NAME				= "SELECT id,name FROM users WHERE name=?;";

	private static final String	GET_USER_PASSWORD				= "SELECT password FROM users WHERE id=?";

	private static final String	CREATE_USER						= "INSERT INTO users (name, password) VALUES(?, ?)";

	private static final String	UPDATE_USER_NAME				= "UPDATE users SET name=? WHERE id=?";

	private static final String	UPDATE_USER_GROUP				= "UPDATE users SET groupid=? WHERE id=?";

	// Groups
	private static final String	GET_GROUP_BY_ID					= "SELECT id,name FROM groups WHERE id=?";

	private static final String	GET_GROUP_BY_NAME				= "SELECT id,name FROM groups WHERE name=?";

	private static final String	GET_GROUP_BY_USER				=
													"SELECT groups.id,groups.name FROM groups INNER JOIN users ON users.groupid=groups.id WHERE users.id=?;";

	private static final String	GET_GROUP_PERMISSIONS			= "SELECT permissions FROM groups WHERE id=?";

	private static final String	CREATE_GROUP					= "INSERT INTO groups (name) VALUES(?)";

	private static final String	CREATE_GROUP_WITH_PERMISSIONS	= "INSERT INTO groups (name, permissions) VALUES(?, ?)";

	private static final String	UPDATE_GROUP_NAME				= "UPDATE groups SET name=? WHERE id=?";

	private static final String	UPDATE_PERMISSIONS				= "UPDATE groups SET permissions=? WHERE id=?";

	public static User getUser(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return new User(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load user #" + id, e);
			return null;
		}
	}

	public static User getUser(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_BY_NAME);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return new User(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load user \"" + name + "\"", e);
			return null;
		}
	}

	public static boolean passwordMatches(User user, String password) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_PASSWORD);
			stmt.setInt(1, user.getId());
			ResultSet rs = stmt.executeQuery();
			return rs.getString("password").equals(UserDatabase.calculatePasswordHash(password));
		} catch (SQLException e) {
			Logger.warn("Failed to verify password for user \"" + user + "\"", e);
			return false;
		}
	}

	public static User createUser(String name, String password) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.CREATE_USER);
			stmt.setString(1, name);
			stmt.setString(2, UserDatabase.calculatePasswordHash(password));
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed create user \"" + name + "\"", e);
		}
		return UserDatabase.getUser(name);
	}

	public static void updateUserName(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_USER_NAME);
			stmt.setString(1, user.getName());
			stmt.setInt(2, user.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed to update group for user " + user, e);
		}
	}

	public static void updateUserGroup(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_USER_GROUP);
			stmt.setInt(1, user.getGroup().getId());
			stmt.setInt(2, user.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed to update group for user " + user, e);
		}
	}

	public static Group getGroup(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_ID);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load group #" + id, e);
			return null;
		}
	}

	public static Group getGroup(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_NAME);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load group \"" + name + "\"", e);
			return null;
		}
	}

	public static Group getGroup(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_USER);
			stmt.setInt(1, user.getId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Group(rs.getInt("id"), rs.getString("name"));
			}
			return null;
		} catch (SQLException e) {
			Logger.warn("Failed to load group for user " + user, e);
			return null;
		}
	}

	public static void updateGroupName(Group group) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_GROUP_NAME);
			stmt.setString(1, group.getName());
			stmt.setInt(2, group.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed to update group for user " + group, e);
		}
	}

	public static PermissionSet getPermissions(Group group) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_PERMISSIONS);
			stmt.setInt(1, group.getId());
			ResultSet rs = stmt.executeQuery();
			return PermissionSet.fromString(rs.getString("permissions"));
		} catch (SQLException e) {
			Logger.warn("Failed to load permissions for group " + group, e);
			return null;
		}
	}

	public static void updatePermissions(Group group) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_PERMISSIONS);
			stmt.setString(1, group.getPermissions().toString());
			stmt.setInt(2, group.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed to update permissions for group " + group, e);
		}
	}

	public static Group createGroup(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.CREATE_GROUP);
			stmt.setString(1, name);
			stmt.executeUpdate();
			return UserDatabase.getGroup(name);
		} catch (SQLException e) {
			Logger.warn("Failed create group \"" + name + "\"", e);
			return null;
		}
	}

	public static Group createGroupWithPermissions(String name, PermissionSet permissions) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.CREATE_GROUP_WITH_PERMISSIONS);
			stmt.setString(1, name);
			stmt.setString(2, permissions.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed create group \"" + name + "\"", e);
		}
		return UserDatabase.getGroup(name);
	}

	private static String calculatePasswordHash(String password) {
		return DigestUtils.sha256Hex(password);
	}
}