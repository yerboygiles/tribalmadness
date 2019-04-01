package com.oddlabs.tt.audio;

import com.oddlabs.tt.resource.NativeResource;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public final strictfp class AudioSource extends NativeResource {
	private final IntBuffer source;
	private AbstractAudioPlayer audio_player;

	public AudioSource() {
		IntBuffer source_buffer = BufferUtils.createIntBuffer(1);
		if (AL.isCreated())
			AL10.alGenSources(source_buffer);
		// if alGenSources fails, the source object will be null (default value)
		source = source_buffer;
	}

	public int getSource() {
		return source.get(0);
	}

	public int getRank() {
		if (audio_player == null)
			return AudioPlayer.AUDIO_RANK_NOT_INITIALIZED;
		return audio_player.getParameters().rank;
	}

	public AbstractAudioPlayer getAudioPlayer() {
		return audio_player;
	}

	public void setAudioPlayer(AbstractAudioPlayer audio_player) {
		if (this.audio_player != null)
			this.audio_player.stop();
		this.audio_player = audio_player;
	}

    @Override
	protected void doDelete() {
		if (source != null && AL.isCreated()) {
			assert AL10.alGetSourcei(getSource(), AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING;
			AL10.alDeleteSources(source);
		}
	}
}
