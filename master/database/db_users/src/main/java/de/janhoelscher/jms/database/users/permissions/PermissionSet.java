package de.janhoelscher.jms.database.users.permissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PermissionSet implements Set<String> {

	private final HashMap<String, Predicate<String>> permissions = new HashMap<>();

	public PermissionSet() {

	}

	public PermissionSet(String... permissions) {
		for (String s : permissions) {
			this.add(s);
		}
	}

	public PermissionSet(Set<String> permissions) {
		this.addAll(permissions);
	}

	@Override
	public int size() {
		return permissions.size();
	}

	@Override
	public boolean isEmpty() {
		return permissions.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (permissions.keySet().contains(o)) {
			return true;
		}
		if (o instanceof String) {
			String str = (String) o;
			return permissions.values().stream().anyMatch(p -> p.test(str));
		}
		return false;
	}

	@Override
	public Iterator<String> iterator() {
		return permissions.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return permissions.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return permissions.keySet().toArray(a);
	}

	@Override
	public boolean add(String e) {
		permissions.put(e, Pattern.compile(e.replace(".", "\\.").replace("*", ".*")).asPredicate());
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return permissions.remove(o) != null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		for (String s : c) {
			this.add(s);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean mod = false;
		for (Object o : c) {
			if (this.remove(o)) {
				mod = true;
			}
		}
		return mod;
	}

	@Override
	public void clear() {
		permissions.clear();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : permissions.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(PermissionSet.permissionSeparator);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	private static final String permissionSeparator = ",";

	public static PermissionSet fromString(String string) {
		return new PermissionSet(string.split(PermissionSet.permissionSeparator));
	}

	public static final PermissionSet	DEFAULT;
	public static final PermissionSet	ADMIN;

	static {
		DEFAULT = new PermissionSet();

		ADMIN = new PermissionSet();
		PermissionSet.ADMIN.add("*");
	}
}