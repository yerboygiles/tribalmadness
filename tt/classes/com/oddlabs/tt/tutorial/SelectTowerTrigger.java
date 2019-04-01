package com.oddlabs.tt.tutorial;

import com.oddlabs.tt.model.Abilities;
import com.oddlabs.tt.model.Building;
import com.oddlabs.tt.model.Race;
import com.oddlabs.tt.player.Player;

public final strictfp class SelectTowerTrigger extends TutorialTrigger {
	private final Building tower = null;
	
	public SelectTowerTrigger(Player player) {
		super(.1f, 0f, "select_tower");
		player.enableRepairing(false);
		player.enableAttacking(false);
		player.enableBuilding(Race.BUILDING_QUARTERS, false);
		player.enableBuilding(Race.BUILDING_ARMORY, false);
	//	player.enableTower(false);
		player.enableHarvesting(false);
		player.enableWeapons(false);
		player.enableArmies(false);
		player.enableTransporting(false);
		player.enableRallyPoints(false);
		player.enableChieftains(false);
		player.enableTowerExits(false);
	}

        @Override
	protected void run(Tutorial tutorial) {
		Building building = tutorial.getViewer().getSelection().getCurrentSelection().getBuilding();
		if (building != null && building.getAbilities().hasAbilities(Abilities.ATTACK)) {
			tutorial.next(new UnitInTowerTrigger(building));
		}
	}
}
