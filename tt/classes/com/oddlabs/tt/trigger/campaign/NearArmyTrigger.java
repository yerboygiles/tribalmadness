package com.oddlabs.tt.trigger.campaign;

import com.oddlabs.tt.model.Selectable;
import com.oddlabs.tt.model.Unit;
import com.oddlabs.tt.pathfinder.FindOccupantFilter;
import com.oddlabs.tt.player.Player;
import com.oddlabs.tt.trigger.IntervalTrigger;
import java.util.List;

public final strictfp class NearArmyTrigger extends IntervalTrigger {
	private final Unit[] src;
	private final float r;
	private final Player player;
	private final Runnable runnable;

	public NearArmyTrigger(Unit[] src, float r, Player player, Runnable runnable) {
		super(player.getWorld(), .25f, 0f);
		this.src = src;
		this.r = r;
		this.player = player;
		this.runnable = runnable;
	}

        @Override
	protected void check() {
            for (Unit src1 : src) {
                if (src1.isDead()) {
                    continue;
                }
                FindOccupantFilter<Selectable> filter = new FindOccupantFilter<>(src1.getPositionX(), src1.getPositionY(), r, src1, Selectable.class);
                player.getWorld().getUnitGrid().scan(filter, src1.getGridX(), src1.getGridY());
                List<Selectable> target_list = filter.getResult();
                for (int j = 0; j < target_list.size(); j++) {
                    Unit unit = (Unit)target_list.get(j);
                    if (!unit.isDead() && unit.getOwner() == player) {
                        triggered();
                        return;
                    }
                }
            }
	}

        @Override
	protected void done() {
		runnable.run();
	}
}
