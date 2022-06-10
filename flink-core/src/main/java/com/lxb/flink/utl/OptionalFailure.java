package com.lxb.flink.utl;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

import com.lxb.flink.utl.function.CheckedSupplier;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class OptionalFailure<T> implements Serializable {
    private transient T value;
    private Throwable failureCause;

    public OptionalFailure(T value, Throwable failureCause) {
        this.value = value;
        this.failureCause = failureCause;
    }

    public static <T> OptionalFailure<T> of(T value) {
        return new OptionalFailure<>(value, null);
    }

    public static <T> OptionalFailure<T> ofFailure(Throwable failureCause) {
        return new OptionalFailure<>(null, failureCause);
    }

    public static <T> OptionalFailure<T> createFrom(CheckedSupplier<T> valueSupplier) {
        try {
            return of(valueSupplier.get());
        } catch (Exception ex) {
            return ofFailure(ex);
        }
    }

    public T get() throws FlinkException {
        if (value != null) {
            return value;
        }
        checkNotNull(failureCause);
        throw new FlinkException(failureCause);
    }

    public T getUnchecked() throws FlinkRuntimeException {
        if (value != null) {
            return value;
        }
        checkNotNull(failureCause);
        throw new FlinkRuntimeException(failureCause);
    }

    public Throwable getFailureCause() {
        return checkNotNull(failureCause);
    }

    public boolean isFailure() {
        return failureCause != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, failureCause);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OptionalFailure<?>)) {
            return false;
        }
        OptionalFailure<?> other = (OptionalFailure<?>) obj;
        return Objects.equals(value, other.value) &&
                Objects.equals(failureCause, other.failureCause);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(value);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        value = (T) stream.readObject();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{value=" + value + ", failureCause=" + failureCause + "}";
    }

}
