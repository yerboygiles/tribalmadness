package com.oddlabs.tt.resource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

public abstract strictfp class NativeResource  {

    /**
     * Resources which have been finalized
     *
     * FIXME this should be replaced with reference queues
     */
    private final static CopyOnWriteArraySet<NativeResource> finalized_resources = new CopyOnWriteArraySet<>();
    /**
     * Count of unfinalized native resources
     */
    private static final AtomicInteger count = new AtomicInteger(0);

    public NativeResource() {
        count.incrementAndGet();
    }

    @Override
    protected final void finalize() throws Throwable {
        try {
            finalized_resources.add(this);
        } finally {
            super.finalize();
        }
    }

    public final static void deleteFinalized() {
        Set<NativeResource> finalized = new HashSet<>(finalized_resources);
        finalized_resources.removeAll(finalized);
        finalized.forEach(r -> {
            count.decrementAndGet();
            r.doDelete();
        });
    }

    public final static void gc() {
        System.gc();
        Runtime.getRuntime().runFinalization();
        deleteFinalized();
    }

    public final static int getCount() {
        return count.get();
    }

    protected abstract void doDelete();
}
