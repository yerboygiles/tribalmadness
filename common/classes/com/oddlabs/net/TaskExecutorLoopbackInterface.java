package com.oddlabs.net;

public strictfp interface TaskExecutorLoopbackInterface<T> {
	public void taskCompleted(T result);
	public void taskFailed(Exception e);
}
