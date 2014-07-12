package com.tenjava.entries.Aangiix3.t2;

public class Duel {
	private DuelData d;
	private String opponent;

	public Duel(final DuelData d, final String opponent) {
		this.d = d;
		this.opponent = opponent;
	}
	public DuelData getDuelData() {
		return d;
	}
	public String getOpponent() {
		return opponent;
	}
}