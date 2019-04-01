package com.oddlabs.tt.util;

public final strictfp class StatCounter {
	private final long[] values;
	private long old_val = 0;
	private long sum = 0;
	private int position = 0;

	public StatCounter(int average_count) {
		values = new long[average_count];
	}

	public void updateDelta(long val) {
		long diff = val - old_val;
		old_val = val;
		updateAbsolute(diff);
	}

	public void updateAbsolute(long val) {
		sum += val - values[position];
		values[position] = val;
		position = (position + 1)%values.length;
	}

	public long getMax() {
		long max = Long.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}
	
	public long getAveragePerUpdateFloored() {
		return sum/values.length;
	}
	
	public float getAveragePerUpdate() {
		return (float)sum/values.length;
	}
}
