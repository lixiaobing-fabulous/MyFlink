package com.lxb.flink.api.common.time;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class Deadline {
    private final long timeNanos;

    private Deadline(long deadline) {
        this.timeNanos = deadline;
    }

    public Deadline plus(Duration other) {
        return new Deadline(Math.addExact(timeNanos, other.toNanos()));
    }

    public Duration timeLeft() {
        return Duration.ofNanos(Math.subtractExact(timeNanos, System.nanoTime()));
    }

    public Duration timeLeftIfAny() throws TimeoutException {
        long nanos = Math.subtractExact(timeNanos, System.nanoTime());
        if (nanos <= 0) {
            throw new TimeoutException();
        }
        return Duration.ofNanos(nanos);
    }

    public boolean hasTimeLeft() {
        return !isOverdue();
    }

    public boolean isOverdue() {
        return timeNanos < System.nanoTime();
    }

    public static Deadline now() {
        return new Deadline(System.nanoTime());
    }

    public static Deadline fromNow(Duration duration) {
        return new Deadline(Math.addExact(System.nanoTime(), duration.toNanos()));
    }

}
