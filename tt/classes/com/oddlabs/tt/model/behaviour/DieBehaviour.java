package com.oddlabs.tt.model.behaviour;

import com.oddlabs.tt.model.Selectable;
import com.oddlabs.tt.model.Unit;

public final strictfp class DieBehaviour implements Behaviour {
	private final static float SECONDS_PER_DEATH = 3f;
	private final static float LYING_SECONDS = 1f;
	private final static float MOVING_SECONDS = 60f;
	
	private final static int MOVING_METERS = 3;

	private final static int DYING = 1;
	private final static int LYING = 2;
	private final static int MOVING = 3;

	private final Unit unit;
	private float anim_time;
	private int state;

	private float offset_z = 0;
	private float dz = 0;

	public DieBehaviour(Unit unit) {
		this.unit = unit;
		init();
	}

        @Override
	public int animate(float t) {
		anim_time -= t;
		offset_z -= dz*t;
		if (anim_time < 0)
			switchState();
		return Selectable.UNINTERRUPTIBLE;
	}

	private void switchState() {
		switch (state) {
			case DYING:
				anim_time += LYING_SECONDS;
				state = LYING;
				break;
			case LYING:
				anim_time += MOVING_SECONDS;
				state = MOVING;
				dz = MOVING_METERS/MOVING_SECONDS;
				break;
			case MOVING:
				unit.remove();
				break;
			default:
				assert false;
		}
	}

	public float getOffsetZ() {
		return offset_z;
	}

        @Override
	public boolean isBlocking() {
		throw new RuntimeException();
	}

	private void init() {
		anim_time = SECONDS_PER_DEATH;
		state = DYING;
		unit.switchAnimation(1f/anim_time, Unit.ANIMATION_DYING);
	}

        @Override
	public void forceInterrupted() {
	}
}
