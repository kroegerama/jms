package de.janhoelscher.jms.database.users;

public class User {

	private final int		id;
	private final String	name;
	private Group			group;

	protected User(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
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
		if (this.group.getId() != group.getId()) {
			UserDatabase.updateUserGroup(this);
		}
	}

	public boolean hasPermission(String permission) {
		return group.hasPermission(permission);
	}

	@Override
	public int hashCode() {
		return id;
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
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return getGroup().getId() == other.getGroup().getId();
	}
}