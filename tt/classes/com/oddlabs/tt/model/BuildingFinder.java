package com.oddlabs.tt.model;

import com.oddlabs.tt.pathfinder.FinderFilter;
import com.oddlabs.tt.pathfinder.Occupant;
import com.oddlabs.tt.pathfinder.Region;
import com.oddlabs.tt.player.Player;
import java.util.List;

public final strictfp class BuildingFinder implements FinderFilter {
	private final Player owner;
	private final int abilities;

	public BuildingFinder(Player owner, int abilities) {
		this.owner = owner;
		this.abilities = abilities;
	}

        @Override
	public Occupant getOccupantFromRegion(Region region, boolean one_region) {
		List buildings = region.getObjects(Building.class);
		for (int i = 0; i < buildings.size(); i++) {
			Building b = (Building)buildings.get(i);
			if (accept(b))
				return b;
		}
		return null;
	}

        @Override
	public Occupant getBest() {
		return null;
	}

	private boolean accept(Building building) {
		return building.getOwner() == owner && building.getAbilities().hasAbilities(abilities);
	}

        @Override
	public boolean acceptOccupant(Occupant occ) {
		if (occ instanceof Building) {
			Building occ_building = (Building)occ;
			return accept(occ_building);
		} else
			return false;
	}
}
