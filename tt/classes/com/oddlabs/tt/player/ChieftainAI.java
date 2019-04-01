package com.oddlabs.tt.player;

import com.oddlabs.tt.model.Unit;
import java.util.Set;

public strictfp abstract class ChieftainAI {
	public abstract void decide(Unit chieftain);

	protected final int numEnemyUnits(Player owner) {
		Player[] players = owner.getWorld().getPlayers();
		int count = 0;
            for (Player player : players) {
                if (owner.isEnemy(player)) {
                    Set units = player.getUnits().getSet();
                    count += units.size();
                }
            }
		return count;
	}
}
