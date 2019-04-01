package com.oddlabs.tt.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a cache of resources by their descriptors
 */
public final strictfp class Resources {
	private final static Map<ResourceDescriptor<?>, Object> loaded_resources = new HashMap<>();

	public static <R> R findResource(ResourceDescriptor<R> resdesc) {
        return (R) loaded_resources.computeIfAbsent(resdesc, res -> res.newInstance());
	}

    private Resources() {
    }
}
