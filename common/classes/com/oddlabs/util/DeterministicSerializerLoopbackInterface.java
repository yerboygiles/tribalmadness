package com.oddlabs.util;

public strictfp interface DeterministicSerializerLoopbackInterface<T> {
	public void saveSucceeded();
	public void loadSucceeded(T object);
	public void failed(Throwable e);
}
