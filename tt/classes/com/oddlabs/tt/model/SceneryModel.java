package com.oddlabs.tt.model;

import com.oddlabs.tt.animation.Animated;
import com.oddlabs.tt.landscape.World;
import com.oddlabs.tt.pathfinder.Occupant;
import com.oddlabs.tt.pathfinder.UnitGrid;
import com.oddlabs.tt.render.SpriteKey;
import com.oddlabs.tt.util.StateChecksum;

public strictfp class SceneryModel extends Model implements Occupant, ModelToolTip, Animated {
	private final SpriteKey sprite_renderer;
	private final float shadow_diameter;
	private final boolean occupy;
	private final String name;
	private final int animation;
	private final float seconds_per_animation_cycle;
	private float anim_time = 0;

	public SceneryModel(World world, float x, float y, float dir_x, float dir_y, SpriteKey sprite_renderer) {
		this(world, x, y, dir_x, dir_y, sprite_renderer, 0f, false, null);
	}

	public SceneryModel(World world, float x, float y, float dir_x, float dir_y, SpriteKey sprite_renderer, float shadow_diameter, boolean occupy, String name) {
		this(world, x, y, dir_x, dir_y, sprite_renderer, shadow_diameter, occupy, name, -1, -1, 0);
	}

	public SceneryModel(World world, float x, float y, float dir_x, float dir_y, SpriteKey sprite_renderer, float shadow_diameter, boolean occupy, String name, int animation, float seconds_per_animation_cycle, float anim_offset) {
		super(world);
		this.sprite_renderer = sprite_renderer;
		this.shadow_diameter = shadow_diameter;
		this.occupy = occupy;
		this.name = name;
		this.animation = animation;
		this.seconds_per_animation_cycle = seconds_per_animation_cycle;
		anim_time = anim_offset;
		setPosition(x, y);
		setDirection(dir_x, dir_y);
		doRegister();
		if (occupy) {
			world.getUnitGrid().occupyGrid(getGridX(), getGridY(), this);
		}
	}

	public final String getName() {
		return name;
	}

        @Override
	public final float getShadowDiameter() {
		return shadow_diameter;
	}

	protected void doRegister() {
		register();
		reinsert();
		getWorld().getNotificationListener().registerTarget(this);
		if (animation > -1)
			getWorld().getAnimationManagerGameTime().registerAnimation(this);
	}

        @Override
	public final void remove() {
		if (occupy) {
			getWorld().getUnitGrid().freeGrid(getGridX(), getGridY(), this);
		}
		super.remove();
		getWorld().getNotificationListener().unregisterTarget(this);
		if (animation > -1)
			getWorld().getAnimationManagerGameTime().removeAnimation(this);
	}

        @Override
	public final void visit(ToolTipVisitor visitor) {
		visitor.visitSceneryModel(this);
	}

        @Override
	public final float getOffsetZ() {
		return 0;
	}

        @Override
	public final void animate(float t) {
		anim_time += t/2.5f;
		if (seconds_per_animation_cycle > -1 && anim_time > seconds_per_animation_cycle)
			anim_time = 0;
		reinsert();
	}
		
        @Override
	public final int getAnimation() {
		if (animation > -1)
			return animation;
		else
			return 0;
	}
	
        @Override
	public final float getAnimationTicks() {
		if (animation > -1) {
			return anim_time;
		} else {
			return 0;
		}
	}

        @Override
	public final void updateChecksum(StateChecksum checksum) {
	}

        @Override
	public final float getNoDetailSize() {
		return 0f;
	}

        @Override
	public int getPenalty() {
		return Occupant.STATIC;
	}

        @Override
	public final int getGridX() {
		return UnitGrid.toGridCoordinate(getPositionX());
	}

        @Override
	public final int getGridY() {
		return UnitGrid.toGridCoordinate(getPositionY());
	}

        @Override
	public final float getSize() {
		throw new RuntimeException();
	}

        @Override
	public final boolean isDead() {
		return false;
	}

	public final boolean isOccupying() {
		return occupy;
	}

        @Override
	public final SpriteKey getSpriteRenderer() {
		return sprite_renderer;
	}

        @Override
	public void visit(ElementVisitor visitor) {
		visitor.visitSceneryModel(this);
	}
}
