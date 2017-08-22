package de.janhoelscher.jms.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnectionProvider {

	public Connection openConnection(String dbName, String username, String password) throws SQLException;
}