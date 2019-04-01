package com.oddlabs.tt.model;

import com.oddlabs.tt.pathfinder.FinderFilter;
import com.oddlabs.tt.pathfinder.Occupant;
import com.oddlabs.tt.pathfinder.Region;
import com.oddlabs.tt.pathfinder.RegionBuilder;
import java.util.ArrayList;
import java.util.List;

public final strictfp class SupplyFinder<S extends Supply> implements FinderFilter {
	private final Unit unit;
	private final Class<S> supply_class;
	private final List<List<S>> region_list = new ArrayList<>();
	private int max_region_dist_sqr;

	public SupplyFinder(Unit unit, Class<S> supply_class) {
		this.unit = unit;
		this.supply_class = supply_class;
	}

        @Override
	public Occupant getOccupantFromRegion(Region region, boolean one_region) {
		List<S> supplies = region.getObjects(supply_class);
		if (one_region) {
			if (supplies.size() > 0) {
				Supply supply = findClosest(supplies);
				assert !supply.isEmpty();
				return supply;
			}

		} else {
			int dx = region.getGridX() - unit.getGridX();
			int dy = region.getGridY() - unit.getGridY();
			int region_dist_sqr =  dx*dx + dy*dy;
			if (supplies.size() > 0) {
				if (region_list.isEmpty()) {
					int region_dist = (int)StrictMath.sqrt(region_dist_sqr);
					int max_region_dist = region_dist + RegionBuilder.REGION_PATH_MAX_COST/2;
					max_region_dist_sqr = max_region_dist*max_region_dist;
				}
				region_list.add(supplies);
			}
			if (region_list.size() > 0 && region_dist_sqr > max_region_dist_sqr) {
				Supply supply = findClosest();
				assert !supply.isEmpty();
				return supply;
			}
		}
		return null;
	}

        @Override
	public Occupant getBest() {
		return findClosest();
	}

	private Supply findClosest(List supplies) {
		int min_dist = Integer.MAX_VALUE;
		Supply closest = null;
		for (int i = 0; i < supplies.size(); i++) {
			Supply current = (Supply)supplies.get(i);
			int dx = current.getGridX() - unit.getGridX();
			int dy = current.getGridY() - unit.getGridY();
			int dist = dx*dx + dy*dy;
			if (min_dist > dist) {
				min_dist = dist;
				closest = current;
			}
		}
		return closest;
	}

	private Supply findClosest() {
		int min_dist = Integer.MAX_VALUE;
		Supply closest = null;
		for (int j = 0; j < region_list.size(); j++) {
			List supplies = region_list.get(j);
			for (int i = 0; i < supplies.size(); i++) {
				Supply current = (Supply)supplies.get(i);
				int dx = current.getGridX() - unit.getGridX();
				int dy = current.getGridY() - unit.getGridY();
				int dist = dx*dx + dy*dy;
				if (min_dist > dist) {
					min_dist = dist;
					closest = current;
				}
			}
		}
		region_list.clear();
		return closest;
	}

        @Override
	public boolean acceptOccupant(Occupant occ) {
		if (supply_class.isInstance(occ)) {
			Supply supply = (Supply)occ;
			assert !supply.isEmpty();
			return true;
		} else
			return false;
	}
}
