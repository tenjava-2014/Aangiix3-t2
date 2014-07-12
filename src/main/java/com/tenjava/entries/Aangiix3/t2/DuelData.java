package com.tenjava.entries.Aangiix3.t2;

public class DuelData {
	private int kills, deaths, points;

	public DuelData(final int kills, final int deaths, final int points) {
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
	}
	public int getKills() {
		return kills;
	}
	public int getDeaths() {
		return deaths;
	}
	public int getPoints() {
		return points;
	}
	public void addKill() {
		kills++;
	}
	public void addDeath() {
		deaths++;
	}
	public void addPoints(int points) {
		points++;
	}
}
