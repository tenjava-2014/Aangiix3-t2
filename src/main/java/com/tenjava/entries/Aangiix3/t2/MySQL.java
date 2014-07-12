package com.tenjava.entries.Aangiix3.t2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQL {
	private Connection conn;

	public MySQL(final String url, final String user, final String pw) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, pw);
			conn.createStatement().execute("CREATE TABLE IF NOT EXISTS DuelStats (" +
					"id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
					"uuid BINARY(16) UNIQUE," +
					"player VARCHAR(255)," +
					"kills INT," +
					"deaths INT," +
					"points INT" +
					")");
		} catch (final Exception ex) {
			System.err.println("> Error loading the MySQL Database!");
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
	public DuelData loadPlayer(final UUID uuid) {
		try {
			final PreparedStatement sel = conn.prepareStatement("SELECT player, kills, deaths, points FROM DuelStats WHERE uuid = UNHEX(?)");
			sel.setString(1, uuid.toString().replaceAll("-", ""));
			final ResultSet rs = sel.executeQuery();
			rs.last();
			if (rs.getRow() != 0) {
				return new DuelData(uuid, rs.getString("player"), rs.getInt("kills"), rs.getInt("deaths"), rs.getInt("points"));
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void savePlayer(final DuelData d) {
		try {
			final PreparedStatement set = conn.prepareStatement("INSERT INTO DuelStats SET uuid=UNHEX(?), player=?, kills=?, deaths=?, points=? ON DUPLICATE KEY UPDATE player=?, kills=?, deaths=?, points=?");
			set.setString(1, d.getUUID().toString().replaceAll("-", ""));
			set.setString(2, d.getName());
			set.setInt(3, d.getKills());
			set.setInt(4, d.getDeaths());
			set.setInt(5, d.getPoints());
			set.setString(6, d.getName());
			set.setInt(7, d.getKills());
			set.setInt(8, d.getDeaths());
			set.setInt(9, d.getPoints());
			set.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
}
