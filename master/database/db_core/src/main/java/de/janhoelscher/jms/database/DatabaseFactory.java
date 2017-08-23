package de.janhoelscher.jms.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.logging.LogFactory;

import de.janhoelscher.jms.config.Config;

public class DatabaseFactory {

	private static DatabaseConnectionProvider	connectionProvider;

	private static Database						database;

	private static DatabaseConnectionProvider getConnectionProvider() {
		if (DatabaseFactory.connectionProvider == null) {
			Iterator<DatabaseConnectionProvider> it = ServiceLoader.load(DatabaseConnectionProvider.class).iterator();
			if (it.hasNext()) {
				DatabaseFactory.connectionProvider = it.next();
			} else {
				DatabaseFactory.connectionProvider = new DefaultDatabaseConnectionProvider();
			}
		}
		return DatabaseFactory.connectionProvider;
	}

	public static Database getDatabase() {
		if (DatabaseFactory.database == null) {
			DatabaseFactory.database = new Database(DatabaseFactory.openConnection(Config.getInstance().Database.Name, null, null));
		}
		return DatabaseFactory.database;
	}

	private static Connection openConnection(String dbName, String username, String password) {
		try {
			return DatabaseFactory.getConnectionProvider().openConnection(dbName, username, password);
		} catch (SQLException e) {
			LogFactory.getLog(DatabaseFactory.class).error("Failed to open connection to database \"" + dbName + "\".", e);
		}
		return null;
	}

	public static void closeDatabase() {
		try {
			DatabaseFactory.database.getConnection().close();
		} catch (SQLException e) {
			LogFactory.getLog(DatabaseFactory.class).warn("Failed to close database connection!", e);
		}
		DatabaseFactory.database = null;
	}
}