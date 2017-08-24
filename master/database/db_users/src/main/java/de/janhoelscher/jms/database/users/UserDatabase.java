package de.janhoelscher.jms.database.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.users.permissions.PermissionSet;
import de.janhoelscher.jms.logging.Logger;

public abstract class UserDatabase {

	// Users
	private static final String	GET_USER_BY_NAME				= "SELECT name FROM users WHERE name=?;";

	private static final String	GET_USER_PASSWORD				= "SELECT password FROM users WHERE name=?";

	private static final String	CREATE_USER						= "INSERT INTO users (name, password) VALUES(?, ?)";

	private static final String	UPDATE_USER_GROUP				= "UPDATE users SET groupname=? WHERE name=?";

	// Groups
	private static final String	GET_GROUP_BY_NAME				= "SELECT name FROM groups WHERE name=?";

	private static final String	GET_GROUP_BY_USER				=
													"SELECT name FROM (groups INNER JOIN users ON users.groupname=groups.name AND users.name=?);";

	private static final String	GET_PERMISSIONS					= "SELECT permissions FROM groups WHERE name=?";

	private static final String	CREATE_GROUP					= "INSERT INTO groups (name) VALUES(?)";

	private static final String	CREATE_GROUP_WITH_PERMISSIONS	= "INSERT INTO groups (name, permissions) VALUES(?, ?)";

	private static final String	UPDATE_PERMISSIONS				= "UPDATE groups SET permissions=? WHERE name=?";

	public static User getUser(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_BY_NAME);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return new User(rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load user with Name \"" + name + "\"", e);
			return null;
		}
	}

	public static boolean passwordMatches(User user, String passwordHash) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_PASSWORD);
			stmt.setString(1, user.getName());
			ResultSet rs = stmt.executeQuery();
			return rs.getString("password").equals(passwordHash);
		} catch (SQLException e) {
			Logger.warn("Failed to verify password for user \"" + user + "\"", e);
			return false;
		}
	}

	public static User createUser(String name, String passwordHash) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.CREATE_USER);
			stmt.setString(1, name);
			stmt.setString(2, passwordHash);
			stmt.executeUpdate();
			return UserDatabase.getUser(name);
		} catch (SQLException e) {
			Logger.warn("Failed create user \"" + name + "\"", e);
			return null;
		}
	}

	public static void updateUserGroup(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_USER_GROUP);
			stmt.setString(1, user.getGroup().getName());
			stmt.setString(2, user.getName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logger.warn("Failed to update group for user " + user, e);
		}
	}

	public static Group getGroup(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_NAME);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load group with name \"" + name + "\"", e);
			return null;
		}
	}

	public static Group getGroup(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_USER);
			stmt.setString(1, user.getName());
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getString("name"));
		} catch (SQLException e) {
			Logger.warn("Failed to load group for user " + user, e);
			return null;
		}
	}

	public static PermissionSet getPermissions(Group group) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_PERMISSIONS);
			stmt.setString(1, group.getName());
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
			stmt.setString(2, group.getName());
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
			return UserDatabase.getGroup(name);
		} catch (SQLException e) {
			Logger.warn("Failed create group \"" + name + "\"", e);
			return null;
		}
	}
}