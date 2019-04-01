package com.oddlabs.tt.net;

import com.oddlabs.util.HashTable;
import java.util.HashMap;

public final strictfp class DistributableTable {
	private final HashTable distributables = new HashTable();
	private final HashMap names = new HashMap();
	private int current_name = 1;

	public int register(Distributable distributable) {
		int name = current_name++;
		Object o = distributables.put(name, distributable);
		assert o == null: "Error registering distributable.";
		Object p = names.put(distributable, name);
		assert p == null: "Error registering name.";
		return name;
	}

	public void unregister(Distributable distributable) {
		Integer name = (Integer)names.remove(distributable);
		assert name != null: "Error unregistering name.";

		Object o = distributables.remove(name);
		assert o == distributable: "Error unregistering distributable.";
	}

	public int getName(Distributable distributable) {
		Integer val = (Integer)names.get(distributable);
		assert val != null : distributable + " is not registrered.";
		return val;
	}

	public Distributable getDistributable(int name) {
		return (Distributable)distributables.get(name);
	}
}
