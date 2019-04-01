package com.oddlabs.tt.guievent;

@FunctionalInterface
public strictfp interface CloseListener extends EventListener {
	public void closed();
}
