package com.lxb.flink.runtime.concurrent;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ScheduledFutureAdapter<V> implements ScheduledFuture<V> {
    private static final AtomicLong SEQUENCE_GEN = new AtomicLong();
    private final        Future<V>  delegate;
    private final        long       tieBreakerUid;
    private final        long       scheduleTimeNanos;

    public ScheduledFutureAdapter(
            Future<V> delegate,
            long delay,
            TimeUnit timeUnit) {
        this(
                delegate,
                System.nanoTime() + TimeUnit.NANOSECONDS.convert(delay, timeUnit),
                SEQUENCE_GEN.incrementAndGet());
    }

    ScheduledFutureAdapter(
            Future<V> delegate,
            long scheduleTimeNanos,
            long tieBreakerUid) {
        this.delegate = delegate;
        this.scheduleTimeNanos = scheduleTimeNanos;
        this.tieBreakerUid = tieBreakerUid;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(scheduleTimeNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {

        if (o == this) {
            return 0;
        }

        // tie breaking for ScheduledFutureAdapter objects
        if (o instanceof ScheduledFutureAdapter) {
            ScheduledFutureAdapter<?> typedOther = (ScheduledFutureAdapter<?>) o;
            int                       cmp        = Long.compare(scheduleTimeNanos, typedOther.scheduleTimeNanos);
            return cmp != 0 ? cmp : Long.compare(tieBreakerUid, typedOther.tieBreakerUid);
        }

        return Long.compare(getDelay(NANOSECONDS), o.getDelay(NANOSECONDS));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    long getTieBreakerUid() {
        return tieBreakerUid;
    }

    long getScheduleTimeNanos() {
        return scheduleTimeNanos;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduledFutureAdapter<?> that = (ScheduledFutureAdapter<?>) o;
        return tieBreakerUid == that.tieBreakerUid && scheduleTimeNanos == that.scheduleTimeNanos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tieBreakerUid, scheduleTimeNanos);
    }


}
