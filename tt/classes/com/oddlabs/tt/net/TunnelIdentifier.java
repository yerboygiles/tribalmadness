package com.oddlabs.tt.net;

import com.oddlabs.matchmaking.Profile;
import com.oddlabs.matchmaking.TunnelAddress;

public final strictfp class TunnelIdentifier {
	private final Profile profile;
	private final TunnelAddress address;

	public TunnelIdentifier(Profile profile, TunnelAddress address) {
		this.profile = profile;
		this.address = address;
	}

	public Profile getProfile() {
		return profile;
	}

	public TunnelAddress getAddress() {
		return address;
	}

        @Override
	public String toString() {
		return "profile: " + profile + " tunnel address: " + address;
	}
}
