package com.tenjava.entries.Aangiix3.t2;

import java.util.UUID;

public class Duel {
	private DuelData d;
	private UUID uuid, opponent;

	public Duel(final DuelData d, final UUID uuid, final UUID opponent) {
		this.d = d;
		this.uuid = uuid;
		this.opponent = opponent;
	}
	public DuelData getDuelData() {
		return d;
	}
	public UUID getUUID() {
		return uuid;
	}
	public UUID getOpponent() {
		return opponent;
	}
}