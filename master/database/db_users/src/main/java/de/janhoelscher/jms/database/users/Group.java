package de.janhoelscher.jms.database.users;

import de.janhoelscher.jms.database.users.permissions.PermissionSet;

public class Group {

	private final String	name;

	private PermissionSet	permissions;

	protected Group(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public PermissionSet getPermissions() {
		if (permissions == null) {
			return UserDatabase.getPermissions(this);
		}
		return permissions;
	}

	public void setPermissions(PermissionSet permissions) {
		this.permissions = permissions;
		if (this.permissions.equals(permissions)) {
			return;
		}
		UserDatabase.updatePermissions(this);
	}

	public boolean hasPermission(String permission) {
		return permissions.contains(permission);
	}
}