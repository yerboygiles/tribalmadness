package com.oddlabs.tt.event;

import com.oddlabs.event.*;
import com.oddlabs.tt.animation.AnimationManager;
import com.oddlabs.tt.util.StateChecksum;
import java.io.File;

public final strictfp class LocalEventQueue {
	private final static LocalEventQueue queue_instance = new LocalEventQueue();

	private final StateChecksum checksum = new StateChecksum();
	private final AnimationManager manager = new AnimationManager();
	private final AnimationManager high_precision_manager = new AnimationManager();
	private Deterministic deterministic;
	private float time = 0;

	public float getTime() {
		return time;
	}

	public long getMillis() {
		return high_precision_manager.getTick()*AnimationManager.ANIMATION_MILLISECONDS_PER_PRECISION_TICK;
	}

	public static LocalEventQueue getQueue() {
		return queue_instance;
	}

	public void setEventsLogged(File log_file) {
		assert deterministic == null;
		this.deterministic = new SaveDeterministic(log_file);
	}

	public void dispose() {
		if (deterministic != null)
			deterministic.endLog();
		deterministic = null;
	}

//public static Deterministic stack_deterministic;
	public void loadEvents(File log_file, boolean zipped) {
		this.deterministic = new LoadDeterministic(log_file, zipped);
/*		File stack_file = new File("stack.log");
		if (stack_file.exists())
			stack_deterministic = new LoadDeterministic(stack_file, false);
		else
			stack_deterministic = new SaveDeterministic(stack_file);
		this.deterministic = new StackTraceDeterministic(deterministic, stack_deterministic);*/
	}

	public AnimationManager getHighPrecisionManager() {
		return high_precision_manager;
	}

	public AnimationManager getManager() {
		return manager;
	}

	public int computeChecksum() {
		checksum.update(getManager().getTick());
		checksum.update(getHighPrecisionManager().getTick());
		manager.updateChecksum(checksum);
		high_precision_manager.updateChecksum(checksum);
		return checksum.getValue();
	}

	public Deterministic getDeterministic() {
		return deterministic;
	}

	public void tickHighPrecision(float t) {
		time += t;
		getHighPrecisionManager().runAnimations(t);
	}

	public void tickLowPrecision(float t) {
		getManager().runAnimations(t);
	}

	public void debugPrintAnimations() {
		getManager().debugPrintAnimations();
	}
}
