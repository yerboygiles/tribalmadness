package com.oddlabs.tt.procedural;

import com.oddlabs.procedural.Channel;
import com.oddlabs.procedural.Layer;
import com.oddlabs.tt.render.Texture;
import com.oddlabs.tt.resource.GLImage;
import com.oddlabs.tt.resource.GLIntImage;
import java.util.Arrays;
import org.lwjgl.opengl.GL11;

public final strictfp class GeneratorHalos extends TextureGenerator {
	public static final int SHADOWED = 0;
	public static final int SELECTED = 1;
	private final int size;
	private final float[][] shadow_parms;
	private final float[][] ring_parms;

	public GeneratorHalos(int size, float[][] shadow_parms, float[][] ring_parms) {
		this.size = size;
		this.shadow_parms = shadow_parms;
		this.ring_parms = ring_parms;
	}

        @Override
	public Texture[] generate() {
		Channel channel_shadow = new Ring(size, size, shadow_parms, Ring.SMOOTH).toChannel();
		Channel channel_ring = new Ring(size, size, ring_parms, Ring.LINEAR).toChannel();
		Channel channel_black = new Channel(size, size).fill(0f);
		Channel channel_white = new Channel(size, size).fill(1f);
		Layer layers[] = new Layer[2];
		layers[SHADOWED] = new Layer(channel_black, channel_black, channel_black, channel_shadow);
		layers[SELECTED] = new Layer(channel_white.copy(), channel_white.copy(), channel_white.copy(), channel_ring);
		layers[SELECTED] = layers[SHADOWED].copy().layerBlend(layers[SELECTED]);
		Texture[] textures = new Texture[layers.length];
		for (int i = 0; i < layers.length; i++) {
			if (Landscape.DEBUG) new GLIntImage(layers[i]).saveAsPNG("generator_halos_" + i);
			textures[i] = new Texture(new GLImage[]{new GLIntImage(layers[i])}, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, GL11.GL_CLAMP, GL11.GL_CLAMP);
		}
		return textures;
	}
	
        @Override
	public int hashCode() {
		return size*Arrays.deepHashCode(shadow_parms)*Arrays.deepHashCode(ring_parms);
	}

        @Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		GeneratorHalos other = (GeneratorHalos)o;
		return size == other.size && Arrays.equals(shadow_parms, other.shadow_parms) && Arrays.equals(ring_parms, other.ring_parms);
	}
}
