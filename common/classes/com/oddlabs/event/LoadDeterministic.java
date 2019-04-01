package com.oddlabs.event;

import java.io.*;
import java.net.URI;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.*;

public final strictfp class LoadDeterministic extends Deterministic {
	private final ReadableByteChannel channel;
	private final ByteBuffer buffer;

	private final ByteBufferInputStream byte_buffer_input_stream = new ByteBufferInputStream();

	private int total_bytes_read;
	private int num_defaults = MIN_DEFAULTS;

	public LoadDeterministic(File logging_file, boolean zipped) {
		try {
			buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
			if (zipped)
				channel = Channels.newChannel(new GZIPInputStream(new FileInputStream(logging_file)));
			else
				channel = new FileInputStream(logging_file).getChannel();
			buffer.limit(0);
			System.out.println("Reading log from " + logging_file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		isDefault(0);
		getDefaults();
	}

        @Override
	public boolean isPlayback() {
		return true;
	}

        @Override
	public void endLog() {
//		assert isEndOfLog();
		try {
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isDefault(int num_bytes) {
		if (num_defaults > MIN_DEFAULTS) {
			num_defaults--;
			if (isEndOfLog())
				System.out.println("***** End of log ***** ");
			return true;
		} else {
			fillBuffer(num_bytes);
			return false;
		}
	}

	private void fillBuffer(int num_bytes) {
		while (num_bytes > buffer.remaining()) {
			fillBuffer();
		}
	}

	private void fillBuffer() {
		if (tryFillBuffer())
			throw new IllegalStateException("End of log reached, bytes read: " + total_bytes_read);
	}

	private boolean tryFillBuffer() {
		try {
			buffer.compact();
			int bytes_read;
			int current_total = 0;
			do {
				bytes_read = channel.read(buffer);
				if (bytes_read != -1) {
					current_total += bytes_read;
				}
			} while (bytes_read != -1 && buffer.hasRemaining());
			buffer.flip();
			total_bytes_read += current_total;
			return bytes_read == -1 && current_total == 0;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isEndOfLog() {
		if (!buffer.hasRemaining() && num_defaults == MIN_DEFAULTS)
			return tryFillBuffer();
		else
			return false;
	}

	private void getDefaults() {
		fillBuffer(DEFAULTS_SIZE);
		num_defaults = buffer.getShort();
		if (isEndOfLog())
			System.out.println("***** End of log *****");
	}

        @Override
	protected byte log(byte b, byte def) {
		if (isDefault(1))
			return def;
		else {
			b = buffer.get();
			getDefaults();
			return b;
		}
	}

        @Override
	protected char log(char c, char def) {
		if (isDefault(2))
			return def;
		else {
			c = buffer.getChar();
			getDefaults();
			return c;
		}
	}

        @Override
	protected int log(int i, int def) {
		if (isDefault(4))
			return def;
		else {
			i = buffer.getInt();
			getDefaults();
			return i;
		}
	}

        @Override
	protected long log(long l, long def) {
		if (isDefault(8))
			return def;
		else {
			l = buffer.getLong();
			getDefaults();
			return l;
		}
	}

        @Override
	protected float log(float f, float def) {
		if (isDefault(4))
			return def;
		else {
			f = buffer.getFloat();
			getDefaults();
			return f;
		}
	}

    protected Path log(Path p, Path def) {
        try {
            URI path = logObject(def.toUri());
            return Paths.get(path);
        } catch(Throwable all) {
            return def;
        }
    }

        @Override
	protected <T> T logObject(T o) {
		try (ObjectInputStream object_input_stream = new ObjectInputStream(byte_buffer_input_stream)) {
			o = (T) object_input_stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
                return o;
	}

        @Override
	protected void logBuffer(ByteBuffer b) {
		boolean isdefault = isDefault(0);
		assert !isdefault;
		while (true) {
			int old_limit = buffer.limit();
			buffer.limit(old_limit - Math.max(0, buffer.remaining() - b.remaining()));
			b.put(buffer);
			buffer.limit(old_limit);
			if (!b.hasRemaining())
				break;
			fillBuffer();
		}
		getDefaults();
	}

	public final strictfp class ByteBufferInputStream extends InputStream {
                @Override
		public int read() throws IOException {
			byte b = log((byte)0);
			return ((int)b) & 0xFF;
		}
	}
}
