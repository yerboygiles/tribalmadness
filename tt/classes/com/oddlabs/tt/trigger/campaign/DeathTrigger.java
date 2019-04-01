package com.oddlabs.tt.trigger.campaign;

import com.oddlabs.tt.model.Selectable;
import com.oddlabs.tt.trigger.IntervalTrigger;

public final strictfp class DeathTrigger extends IntervalTrigger {
	private final Selectable selectable;
	private final Runnable runnable;

	public DeathTrigger(Selectable selectable, Runnable runnable) {
		super(selectable.getOwner().getWorld(), .5f, 0f);
		this.selectable = selectable;
		this.runnable = runnable;
	}

        @Override
	protected void check() {
		if (selectable.isDead())
			triggered();
	}

        @Override
	protected void done() {
		runnable.run();
	}
}
