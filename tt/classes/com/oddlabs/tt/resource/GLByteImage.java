package com.oddlabs.tt.resource;

import com.oddlabs.procedural.Channel;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final strictfp class GLByteImage extends GLImage {

    @Override
    public int getPixelSize() {
        return 1;
    }

    public GLByteImage(int width, int height, ByteBuffer pixel_data, int format) {
        super(width, height, pixel_data, format);
    }

    public GLByteImage(int width, int height, int format) {
        this(width, height, BufferUtils.createByteBuffer(width * height), format);
    }

    public GLByteImage(Channel channel) {
        this(channel, GL11.GL_ALPHA);
    }

    public GLByteImage(Channel channel, int format) {
        this(channel.getWidth(), channel.getHeight(), format);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int pixel = StrictMath.round(channel.getPixel(x, y) * 255);
                putPixel(x, y, Math.min(Math.max(0, pixel), 255));
            }
        }
    }

    @Override
    public GLImage createImage(int width, int height, int format) {
        return new GLByteImage(width, height, format);
    }

    @Override
    public int getPixel(int x, int y) {
        return getPixels().get(y * getWidth() + x) & 0xff;
    }

    @Override
    public void putPixel(int x, int y, int pixel) {
        getPixels().put(y * getWidth() + x, (byte) pixel);
    }
}
