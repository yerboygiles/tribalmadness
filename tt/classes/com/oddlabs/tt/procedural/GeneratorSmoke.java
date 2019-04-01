package com.oddlabs.tt.procedural;

import com.oddlabs.procedural.Channel;
import com.oddlabs.procedural.Layer;
import com.oddlabs.tt.global.*;
import com.oddlabs.tt.render.*;
import com.oddlabs.tt.resource.*;
import org.lwjgl.opengl.*;

public final strictfp class GeneratorSmoke extends TextureGenerator {
	private static final int TEXTURE_SIZE = 128;

        @Override
	public Texture[] generate() {
		Channel voronoi = new Voronoi(TEXTURE_SIZE, 4, 4, 1, 1f, 42).getDistance(-1f, 0f, 0f);
		Channel smoke_alpha = new Ring(TEXTURE_SIZE, TEXTURE_SIZE, new float[][] {{0f, 1f},{0.5f, 0f}}, Ring.SMOOTH).toChannel().gamma(1.5f);
		Channel smoke_color = new Channel(TEXTURE_SIZE, TEXTURE_SIZE).fill(0.5f);
		Channel smoke_bump = voronoi.gamma(0.25f).smooth(3).smooth(1).dynamicRange(0.925f, 1f).channelMultiply(smoke_alpha);
		smoke_color.bump(smoke_bump, 0f, -4f, 0f, 1f, 0f);
		Layer smoke = new Layer(smoke_color, smoke_color, smoke_color, smoke_alpha);
		GLIntImage smoke_img = new GLIntImage(smoke);
		if (Landscape.DEBUG) smoke_img.saveAsPNG("generator_smoke");
		Texture[] textures = new Texture[1];
		textures[0] = new Texture(smoke_img.createMipMaps(), Globals.COMPRESSED_RGBA_FORMAT, GL11.GL_LINEAR_MIPMAP_NEAREST, GL11.GL_LINEAR, GL11.GL_CLAMP, GL11.GL_CLAMP);
		return textures;
	}

        @Override
	public int hashCode() {
		return TEXTURE_SIZE + 3;
	}
}
