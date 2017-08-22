package de.janhoelscher.jms.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Database {

	private final Connection							connection;

	private final HashMap<String, PreparedStatement>	preparedStatements	= new HashMap<>();

	protected Database(Connection connection) {
		this.connection = connection;
	}

	protected Connection getConnection() {
		return connection;
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		PreparedStatement result = preparedStatements.get(sql);
		if (result == null) {
			result = connection.prepareStatement(sql);
			preparedStatements.put(sql, result);
		}
		return result;
	}

	public boolean execute(String sql) throws SQLException {
		return prepareStatement(sql).execute();
	}

	public int executeUpdate(String sql) throws SQLException {
		return prepareStatement(sql).executeUpdate();
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		return prepareStatement(sql).executeQuery();
	}
}