package com.tenjava.entries.Aangiix3.t2;

import java.util.UUID;

public class DuelData {
	private UUID uuid;
	private String player;
	private int kills, deaths, points;

	public DuelData(final UUID uuid, final String player, final int kills, final int deaths, final int points) {
		this.uuid = uuid;
		this.player = player;
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
	}
	public UUID getUUID() {
		return uuid;
	}
	public String getName() {
		return player;
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
	public void setName(final String player) {
		this.player = player;
	}
	public void addKill() {
		this.kills++;
	}
	public void addDeath() {
		this.deaths++;
	}
	public void addPoints(final int points) {
		this.points += points;
	}
}
