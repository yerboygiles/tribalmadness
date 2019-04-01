package com.oddlabs.tt.audio;

import com.oddlabs.tt.resource.NativeResource;
import com.oddlabs.util.ByteBufferOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public final strictfp class Audio extends NativeResource {
	private final IntBuffer al_buffers;

	public Audio(URL file) throws IOException {
		this(1);
		if (!AL.isCreated())
			return;
		Wave wave;
		try {
			wave = new Wave(file);
		} catch (UnsupportedAudioFileException | IOException e) {
			// Assume it's an ogg vorbis file
			wave = loadOGG(file);
		}
		AL10.alBufferData(al_buffers.get(0), wave.getFormat(), wave.getData(), wave.getSampleRate());
	}

	public Audio(int num_buffers) {
		al_buffers = BufferUtils.createIntBuffer(num_buffers);

		if (!AL.isCreated())
			return;
		AL10.alGenBuffers(al_buffers);
	}

	private Wave loadOGG(URL file) throws IOException {
		ByteBufferOutputStream output = new ByteBufferOutputStream(true);
		OGGStream ogg_stream = new OGGStream(file);
		int channels = ogg_stream.getChannels();
		int rate = ogg_stream.getRate();
		int bytes;
		int total_bytes = 0;
		do {
			bytes = ogg_stream.read(output);
			total_bytes += bytes;
		} while (bytes > 0);
		output.buffer().rewind();
		output.buffer().limit(total_bytes);
		return new Wave(output.buffer(), channels, 16, rate);
	}

	public IntBuffer getBuffers() {
		return al_buffers;
	}

	public int getBuffer() {
		return al_buffers.get(0);
	}

        @Override
	protected void doDelete() {
		if (AL.isCreated()) {
			al_buffers.clear();
			AL10.alDeleteBuffers(al_buffers);
		}
	}
}
