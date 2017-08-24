package de.janhoelscher.jms.database.users;

public class User {

	private final String	name;

	private Group			group;

	protected User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Group getGroup() {
		if (group == null) {
			return UserDatabase.getGroup(this);
		}
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
		if (!this.group.getName().equals(group.getName())) {
			UserDatabase.updateUserGroup(this);
		}
	}

	public boolean hasPermission(String permission) {
		return group.hasPermission(permission);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return getGroup().getName().equals(other.getGroup().getName());
	}
}