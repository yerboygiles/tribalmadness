package com.oddlabs.net;

public strictfp interface Callable<T> extends TaskExecutorLoopbackInterface, java.util.concurrent.Callable<T>{
	/*
	 * This method is called by instances of the AbstractTaskExecutor
	 * subclasses. It is threaded  and, is not deterministic,
	 * so it must _not_ have any side effects!
	 */
    @Override
	public T call() throws Exception;
}
