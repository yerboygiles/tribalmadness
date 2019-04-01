package com.oddlabs.tt.resource;

public strictfp interface ResourceDescriptor<R> {
	public R newInstance();
}
