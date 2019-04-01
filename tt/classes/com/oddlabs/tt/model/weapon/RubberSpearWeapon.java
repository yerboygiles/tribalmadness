package com.oddlabs.tt.model.weapon;

import com.oddlabs.tt.audio.Audio;
import com.oddlabs.tt.model.AttackScanFilter;
import com.oddlabs.tt.model.Selectable;
import com.oddlabs.tt.model.Unit;
import com.oddlabs.tt.player.Player;
import com.oddlabs.tt.render.SpriteKey;

public final strictfp class RubberSpearWeapon extends DirectedThrowingWeapon {
	private final static float METERS_PER_SECOND = 30; //multiplied by meters/second (in 2D)
	private final int MAX_BOUNDS_LENGTH = 3;
	
	public RubberSpearWeapon(boolean hit, Unit src, Selectable target, SpriteKey sprite_renderer, Audio throw_sound, Audio[] hit_sounds) {
		super( hit, src, target, sprite_renderer, throw_sound, hit_sounds);
	}

        @Override
	protected void hitTarget(boolean hit, Player owner, Selectable target) {
		if (hit)
			damageTarget(target);
		AttackScanFilter filter = new AttackScanFilter(owner, MAX_BOUNDS_LENGTH);
		owner.getWorld().getUnitGrid().scan(filter, target.getGridX(), target.getGridY());
		Selectable s = filter.removeTarget();
		if (s != null && owner.getWorld().getRandom().nextFloat() > .5f) {
			setTarget(s);
		} else 
			super.hitTarget(hit, owner, target);
	}


        @Override
	protected float getMetersPerSecond() {
		return METERS_PER_SECOND;
	}

        @Override
	protected int getDamage() {
		return 2;
	}
}
