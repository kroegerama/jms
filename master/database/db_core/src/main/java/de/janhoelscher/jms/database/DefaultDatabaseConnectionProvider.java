package de.janhoelscher.jms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.LogFactory;

public class DefaultDatabaseConnectionProvider implements DatabaseConnectionProvider {

	private static boolean driverLoaded = false;

	private synchronized boolean loadDriver() {
		if (!DefaultDatabaseConnectionProvider.driverLoaded) {
			try {
				Class.forName("org.sqlite.JDBC");
				DefaultDatabaseConnectionProvider.driverLoaded = true;
			} catch (ClassNotFoundException e) {
				LogFactory.getLog(DefaultDatabaseConnectionProvider.class).error(	"Failed to load SQLite JDBC driver!",
																					e);
			}
		}
		return DefaultDatabaseConnectionProvider.driverLoaded;
	}

	@Override
	public Connection openConnection(String dbName, String username, String password) throws SQLException {
		if (loadDriver()) {
			return DriverManager.getConnection("jdbc:sqlite:db/" + dbName + ".db");
		}
		return null;
	}
}