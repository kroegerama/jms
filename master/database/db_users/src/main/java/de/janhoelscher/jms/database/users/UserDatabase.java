package de.janhoelscher.jms.database.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.LogFactory;

import de.janhoelscher.jms.database.DatabaseFactory;
import de.janhoelscher.jms.database.users.permissions.PermissionSet;

public abstract class UserDatabase {
	// Users
	private static final String	GET_USER_BY_ID					= "SELECT id,name FROM users WHERE id=?;";
	private static final String	GET_USER_BY_NAME				= "SELECT id,name FROM users WHERE name=\"?\";";
	private static final String	CREATE_USER						= "INSERT INTO users (name, password) VALUES(?, ?)";
	private static final String	UPDATE_USER_GROUP				= "UPDATE users SET group=? WHERE id=?";

	// Groups
	private static final String	GET_GROUP_BY_ID					= "SELECT id,name FROM groups WHERE id=?;";
	private static final String	GET_GROUP_BY_NAME				= "SELECT id,name FROM groups WHERE name=?;";
	private static final String	GET_GROUP_BY_USER				=
													"SELECT id,name FROM (groups INNER JOIN users ON users.groupid=groups.id AND users.id=?);";
	private static final String	GET_PERMISSIONS					= "SELECT permissions FROM groups WHERE id=?";
	private static final String	CREATE_GROUP					= "INSERT INTO groups (name) VALUES(?)";
	private static final String	CREATE_GROUP_WITH_PERMISSIONS	= "INSERT INTO groups (name, permissions) VALUES(?, ?)";
	private static final String	UPDATE_PERMISSIONS				= "UPDATE groups SET permissions=? WHERE id=?";

	public static User getUser(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_BY_ID);
			stmt.setInt(0, id);
			ResultSet rs = stmt.executeQuery();
			return new User(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to load user with ID " + id, e);
			return null;
		}
	}

	public static User getUser(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_USER_BY_NAME);
			stmt.setString(0, name);
			ResultSet rs = stmt.executeQuery();
			return new User(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to load user with Name \"" + name + "\"", e);
			return null;
		}
	}

	public static User createUser(String name, String passwordHash) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.CREATE_USER);
			stmt.setString(0, name);
			stmt.setString(1, passwordHash);
			stmt.executeUpdate();
			return UserDatabase.getUser(name);
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed create user \"" + name + "\"", e);
			return null;
		}
	}

	public static void updateUserGroup(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_USER_GROUP);
			stmt.setInt(1, user.getId());
			stmt.setInt(0, user.getGroup().getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to update group for user " + user, e);
		}
	}

	public static Group getGroup(int id) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_ID);
			stmt.setInt(0, id);
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to load group with id " + id, e);
			return null;
		}
	}

	public static Group getGroup(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_NAME);
			stmt.setString(0, name);
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to load group with name \"" + name + "\"", e);
			return null;
		}
	}

	public static Group getGroup(User user) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_GROUP_BY_USER);
			stmt.setInt(0, user.getId());
			ResultSet rs = stmt.executeQuery();
			return new Group(rs.getInt("id"), rs.getString("name"));
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to load group for user " + user, e);
			return null;
		}
	}

	public static PermissionSet getPermissions(Group group) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.GET_PERMISSIONS);
			stmt.setInt(0, group.getId());
			ResultSet rs = stmt.executeQuery();
			return PermissionSet.fromString(rs.getString("permissions"));
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to load permissions for group " + group, e);
			return null;
		}
	}

	public static void updatePermissions(Group group) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.UPDATE_PERMISSIONS);
			stmt.setInt(1, group.getId());
			stmt.setString(0, group.getPermissions().toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed to update permissions for group " + group, e);
		}
	}

	public static Group createGroup(String name) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase().prepareStatement(UserDatabase.CREATE_GROUP);
			stmt.setString(0, name);
			stmt.executeUpdate();
			return UserDatabase.getGroup(name);
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed create group \"" + name + "\"", e);
			return null;
		}
	}

	public static Group createGroupWithPermissions(String name, PermissionSet permissions) {
		try {
			PreparedStatement stmt = DatabaseFactory.getDatabase()
													.prepareStatement(UserDatabase.CREATE_GROUP_WITH_PERMISSIONS);
			stmt.setString(0, name);
			stmt.setString(1, permissions.toString());
			stmt.executeUpdate();
			return UserDatabase.getGroup(name);
		} catch (SQLException e) {
			LogFactory.getLog(UserDatabase.class).warn("Failed create group \"" + name + "\"", e);
			return null;
		}
	}
}