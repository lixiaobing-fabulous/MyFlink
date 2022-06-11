package com.lxb.flink.api.common.time;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

public final class Time implements Serializable {
    private final TimeUnit unit;
    private final long     size;

    private Time(long size, TimeUnit unit) {
        this.unit = checkNotNull(unit, "time unit may not be null");
        this.size = size;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public long getSize() {
        return size;
    }

    public long toMilliseconds() {
        return unit.toMillis(size);
    }

    @Override
    public String toString() {
        return toMilliseconds() + " ms";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Time time = (Time) o;
        return toMilliseconds() == time.toMilliseconds();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toMilliseconds());
    }

    public static Time of(long size, TimeUnit unit) {
        return new Time(size, unit);
    }

    public static Time milliseconds(long milliseconds) {
        return of(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static Time seconds(long seconds) {
        return of(seconds, TimeUnit.SECONDS);
    }

    public static Time minutes(long minutes) {
        return of(minutes, TimeUnit.MINUTES);
    }

    public static Time hours(long hours) {
        return of(hours, TimeUnit.HOURS);
    }

    public static Time days(long days) {
        return of(days, TimeUnit.DAYS);
    }

}
