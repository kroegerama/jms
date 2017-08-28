package de.janhoelscher.jms.database.users;

import de.janhoelscher.jms.database.users.permissions.PermissionSet;

public class Group {

	private final int		id;

	private String			name;

	private PermissionSet	permissions;

	protected Group(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
			UserDatabase.updateGroupName(this);
		}
	}

	public PermissionSet getPermissions() {
		if (permissions == null) {
			permissions = UserDatabase.getPermissions(this);
		}
		return permissions;
	}

	public void setPermissions(PermissionSet permissions) {
		if (getPermissions().equals(permissions)) {
			return;
		}
		this.permissions = permissions;
		UserDatabase.updatePermissions(this);
	}

	public boolean hasPermission(String permission) {
		return getPermissions() != null && getPermissions().contains(permission);
	}
}