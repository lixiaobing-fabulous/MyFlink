package com.lxb.flink.utl;

import com.lxb.flink.annotation.VisibleForTesting;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractCloseableRegistry<C extends Closeable, T> implements Closeable {

    private final   Object            lock;
    @GuardedBy("lock")
    protected final Map<Closeable, T> closeableToRef;
    @GuardedBy("lock")
    private         boolean           closed;

    public AbstractCloseableRegistry(@Nonnull Map<Closeable, T> closeableToRef) {
        this.lock = new Object();
        this.closeableToRef = Preconditions.checkNotNull(closeableToRef);
        this.closed = false;
    }

    public final void registerCloseable(C closeable) throws IOException {

        if (null == closeable) {
            return;
        }

        synchronized (getSynchronizationLock()) {
            if (!closed) {
                doRegister(closeable, closeableToRef);
                return;
            }
        }

        IOUtils.closeQuietly(closeable);
        throw new IOException("Cannot register Closeable, registry is already closed. Closing argument.");
    }

    public final boolean unregisterCloseable(C closeable) {

        if (null == closeable) {
            return false;
        }

        synchronized (getSynchronizationLock()) {
            return doUnRegister(closeable, closeableToRef);
        }
    }

    @Override
    public void close() throws IOException {
        Collection<Closeable> toCloseCopy;

        synchronized (getSynchronizationLock()) {

            if (closed) {
                return;
            }

            closed = true;

            toCloseCopy = getReferencesToClose();

            closeableToRef.clear();
        }

        IOUtils.closeAllQuietly(toCloseCopy);
    }

    public boolean isClosed() {
        synchronized (getSynchronizationLock()) {
            return closed;
        }
    }

    protected Collection<Closeable> getReferencesToClose() {
        return new ArrayList<>(closeableToRef.keySet());
    }

    protected abstract void doRegister(@Nonnull C closeable, @Nonnull Map<Closeable, T> closeableMap);

    protected abstract boolean doUnRegister(@Nonnull C closeable, @Nonnull Map<Closeable, T> closeableMap);

    protected final Object getSynchronizationLock() {
        return lock;
    }

    protected final void addCloseableInternal(Closeable closeable, T metaData) {
        synchronized (getSynchronizationLock()) {
            closeableToRef.put(closeable, metaData);
        }
    }

    protected final void removeCloseableInternal(Closeable closeable) {
        synchronized (getSynchronizationLock()) {
            closeableToRef.remove(closeable);
        }
    }

    @VisibleForTesting
    public final int getNumberOfRegisteredCloseables() {
        synchronized (getSynchronizationLock()) {
            return closeableToRef.size();
        }
    }

    @VisibleForTesting
    public final boolean isCloseableRegistered(Closeable c) {
        synchronized (getSynchronizationLock()) {
            return closeableToRef.containsKey(c);
        }
    }

}
