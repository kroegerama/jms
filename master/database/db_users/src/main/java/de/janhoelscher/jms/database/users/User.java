package de.janhoelscher.jms.database.users;

public class User {

	private final int	id;

	private String		name;

	private Group		group;

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

	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
			UserDatabase.updateUserName(this);
		}
	}

	public Group getGroup() {
		if (group == null) {
			group = UserDatabase.getGroup(this);
		}
		return group;
	}

	public void setGroup(Group group) {
		if (getGroup() == null || getGroup().getId() != group.getId()) {
			this.group = group;
			UserDatabase.updateUserGroup(this);
		}
	}

	public boolean hasPermission(String permission) {
		return getGroup() != null && getGroup().hasPermission(permission);
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
		return getGroup().getId() == other.getGroup().getId();
	}
}