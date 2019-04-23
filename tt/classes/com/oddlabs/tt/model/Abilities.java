package com.oddlabs.tt.model;

public final strictfp class Abilities {
    // No abilities
    public final static int NONE = 0;
    // Can build/repair buildings
    public final static int BUILD = 1;
    // Can attack other units
    public final static int ATTACK = 2;
    // Can harvest resources
    public final static int HARVEST = 3;
    // contains supplies of all kinds
    public final static int SUPPLY_CONTAINER = 4;
    // builds warriors
    public final static int BUILD_ARMIES = 5;
    // creates peons
    public final static int REPRODUCE = 6;
    // Can target other units
    public final static int TARGET = 7;
    // Can throw weapon
    public final static int THROW = 8;
    // Can be a rally target
    public final static int RALLY_TO = 9;
    // Can use magic
    public final static int MAGIC = 10;
    // contains wood supplies
    public final static int SUPPLY_CONTAINER_WOOD = 11;
    // contains rock supplies
    public final static int SUPPLY_CONTAINER_ROCK = 12;
    // contains iron supplies
    public final static int SUPPLY_CONTAINER_IRON = 13;
    // contains chicken supplies
    public final static int SUPPLY_CONTAINER_CHICKEN = 14;

    private int abilities;

    public Abilities(int abilities) {
        this.abilities = abilities;
    }

    public boolean hasAbilities(int abilities) {
        return (this.abilities | abilities) == this.abilities;
    }

    public void addAbilities(Abilities abilities) {
        addAbilities(abilities.abilities);
    }

    public void addAbilities(int abilities) {
        this.abilities = this.abilities | abilities;
    }

    public void removeAbilities(Abilities abilities) {
        removeAbilities(abilities.abilities);
    }

    public void removeAbilities(int abilities) {
        this.abilities = this.abilities & ~abilities;
    }
}
