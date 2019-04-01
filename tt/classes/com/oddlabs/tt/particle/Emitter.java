package com.oddlabs.tt.particle;

import com.oddlabs.tt.animation.Animated;
import com.oddlabs.tt.animation.AnimationManager;
import com.oddlabs.tt.landscape.World;
import com.oddlabs.tt.model.Element;
import com.oddlabs.tt.model.ElementVisitor;
import com.oddlabs.tt.render.SpriteKey;
import com.oddlabs.tt.render.TextureKey;
import com.oddlabs.tt.util.StateChecksum;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;

public abstract strictfp class Emitter extends Element implements Animated {
	private final AnimationManager manager;
	private final List<Particle>[] particles;
	private final TextureKey[] textures;
	private final SpriteKey[] sprite_renderers;
	private final int src_blend_func;
	private final int dst_blend_func;
	private final int types;
	private final World world;

	private Vector3f position;
	private float scale_x = 1f;
	private float scale_y = 1f;
	private float scale_z = 1f;

        @SuppressWarnings("unchecked")
	public Emitter(World world, Vector3f position, int src_blend_func, int dst_blend_func, TextureKey[] textures, SpriteKey[] sprite_renderers, int types, AnimationManager manager) {
		super(world.getElementRoot());
		this.world = world;
		this.position = position;
		this.src_blend_func = src_blend_func;
		this.dst_blend_func = dst_blend_func;
		this.textures = textures;
		this.sprite_renderers = sprite_renderers;
		this.types = types;
		this.manager = manager;
		particles = (List<Particle>[])new ArrayList<?>[types];
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new ArrayList<>();
		}
		register();
	}

	public final World getWorld() {
		return world;
	}

	public final SpriteKey[] getSpriteRenderers() {
		return sprite_renderers;
	}

	public final List<Particle>[] getParticles() {
		return particles;
	}

	public final TextureKey[] getTextures() {
		return textures;
	}

	public final int getSrcBlendFunc() {
		return src_blend_func;
	}

	public final int getDstBlendFunc() {
		return dst_blend_func;
	}

	protected final void add(Particle particle) {
		particles[particle.getType()].add(particle);
	}

	public final void setPosition(Vector3f position) {
		this.position = position;
	}

	public final Vector3f getPosition() {
		return position;
	}

	final float getX() {
		return position.getX();
	}

	final float getY() {
		return position.getY();
	}

	final float getZ() {
		return position.getZ();
	}

	public final void scale(float scale_x, float scale_y, float scale_z) {
		this.scale_x = scale_x;
		this.scale_y = scale_y;
		this.scale_z = scale_z;
	}

	public final float getScaleX() {
		return scale_x;
	}

	public final float getScaleY() {
		return scale_y;
	}

	public final float getScaleZ() {
		return scale_z;
	}

	public final void forceColorChange(float dr, float dg, float db, float da) {
            for (List<Particle> particle1 : particles) {
                for (int i = 0; i < particle1.size(); i++) {
                    Particle particle = particle1.get(i);
                    particle.setColor(particle.getColorR() + dr, particle.getColorG() + dg, particle.getColorB() + db, particle.getColorA() + da);
                }
            }
	}

	protected final int getTypes() {
		return types;
	}

        @Override
	protected void register() {
		super.register();
		manager.registerAnimation(this);
	}

        @Override
	public final void visit(ElementVisitor visitor) {
		visitor.visitEmitter(this);
	}

        @Override
	protected final void remove() {
		super.remove();
		manager.removeAnimation(this);
	}

        @Override
	public final void updateChecksum(StateChecksum checksum) {
	}
}
