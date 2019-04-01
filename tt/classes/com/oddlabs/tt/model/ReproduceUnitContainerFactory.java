package com.oddlabs.tt.model;

public final strictfp class ReproduceUnitContainerFactory implements UnitContainerFactory {
        @Override
	public UnitContainer createContainer(Building building) {
		return new ReproduceUnitContainer(building);
	}
}
