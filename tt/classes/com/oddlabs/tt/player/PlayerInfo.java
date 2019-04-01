package com.oddlabs.tt.player;

import java.io.Serializable;

public final strictfp class PlayerInfo implements Serializable {
	private final static long serialVersionUID = 3;

	public final static int TEAM_NEUTRAL = -1;

	private final int race;
	private final String name;
	private final int team;

	public PlayerInfo(int team, int race, String name) {
		this.team = team;
		this.race = race;
		this.name = name;
	}

    @Override
	public boolean equals(Object other) {
		if (!(other instanceof PlayerInfo))
			return false;
		PlayerInfo player = (PlayerInfo)other;
		return team == player.team && race == player.race;
	}

	public int getRace() {
		return race;
	}

	public String getName() {
		return name;
	}

	public int getTeam() {
		return team;
	}

        @Override
	public String toString() {
		return name;
	}
}
