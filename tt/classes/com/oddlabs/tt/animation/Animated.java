package com.oddlabs.tt.animation;

import com.oddlabs.tt.util.*;

/**
 * A user interface element that changes over time
 */
public strictfp interface Animated {
	void animate(float t);
	void updateChecksum(StateChecksum checksum);
}
