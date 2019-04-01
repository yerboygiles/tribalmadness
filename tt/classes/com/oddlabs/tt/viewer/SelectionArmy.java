package com.oddlabs.tt.viewer;

import com.oddlabs.tt.model.Abilities;
import com.oddlabs.tt.model.Army;
import com.oddlabs.tt.model.Building;
import com.oddlabs.tt.model.Selectable;
import com.oddlabs.tt.model.Unit;
import com.oddlabs.tt.player.Player;
import java.util.Iterator;

public final strictfp class SelectionArmy extends Army {

    private final Player local_player;
    private int num_units;
    private int num_builders;
    private Unit chieftain;
    private Building building;

    SelectionArmy(Player local_player) {
        this.local_player = local_player;
    }

    public int getNumBuilders() {
        return num_builders;
    }

    public int getNumUnits() {
        return num_units;
    }

    public Unit getChieftain() {
        return chieftain;
    }

    public Building getBuilding() {
        return building;
    }

    private void update() {
        Iterator it = getSet().iterator();
        num_units = 0;
        num_builders = 0;
        chieftain = null;
        building = null;
        while (it.hasNext()) {
            Selectable s = (Selectable) it.next();
            if (s.getOwner() != local_player)
                continue;
            Abilities abilities = s.getAbilities();
            if (abilities.hasAbilities(Abilities.BUILD))
                num_builders++;
            else
                if (abilities.hasAbilities(Abilities.MAGIC))
                    chieftain = (Unit) s;
            if (s instanceof Building) {
                building = (Building) s;
            } else {
                num_units++;
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        update();
    }

    @Override
    public void remove(Selectable selectable) {
        super.remove(selectable);
        update();
    }

    @Override
    public void add(Selectable selectable) {
        super.add(selectable);
        update();
    }
}
