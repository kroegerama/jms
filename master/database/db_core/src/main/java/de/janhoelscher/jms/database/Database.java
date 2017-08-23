package de.janhoelscher.jms.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Database {

	private final Semaphore								lock				= new Semaphore(1, true);

	private final Connection							connection;

	private final HashMap<String, PreparedStatement>	preparedStatements	= new HashMap<>();

	protected Database(Connection connection) {
		this.connection = connection;
	}

	protected Connection getConnection() {
		return connection;
	}

	public void lock() {
		lock.acquireUninterruptibly();
	}

	public void unlock() {
		lock.release();
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
		return connection.createStatement().execute(sql);
	}

	public int executeUpdate(String sql) throws SQLException {
		return connection.createStatement().executeUpdate(sql);
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		return connection.createStatement().executeQuery(sql);
	}

	public void executeMultiple(String sql) throws SQLException {
		String[] commands = sql.split("\\;");
		Statement stmt = connection.createStatement();
		for (String command : commands) {
			stmt.execute(command);
		}
	}
}