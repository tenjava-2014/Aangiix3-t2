package com.tenjava.entries.Aangiix3.t2;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL {
	private Connection conn;

	public MySQL(final String url, final String user, final String pw) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, pw);
			conn.createStatement().execute("CREATE TABLE IF NOT EXISTS DuelStats (" +
					"id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
					"player VARCHAR(255) UNIQUE," +
					"kills INT," +
					"deaths INT," +
					"points INT" +
					")");
		} catch (final Exception ex) {
			System.err.println("Error loading the MySQL Database!");
			return;
		}
	}

	public void kill() {
		try {
			conn.close();
		} catch (final Exception e) {
			return;
		}
	}
}
