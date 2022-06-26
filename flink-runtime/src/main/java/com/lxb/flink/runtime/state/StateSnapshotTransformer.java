package com.lxb.flink.runtime.state;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Optional;

@FunctionalInterface
@NotThreadSafe
public interface StateSnapshotTransformer<T> {
    @Nullable
    T filterOrTransform(@Nullable T value);
    interface StateSnapshotTransformFactory<T> {
        StateSnapshotTransformFactory<?> NO_TRANSFORM = createNoTransform();

        @SuppressWarnings("unchecked")
        static <T> StateSnapshotTransformFactory<T> noTransform() {
            return (StateSnapshotTransformFactory<T>) NO_TRANSFORM;
        }

        static <T> StateSnapshotTransformFactory<T> createNoTransform() {
            return new StateSnapshotTransformFactory<T>() {
                @Override
                public Optional<StateSnapshotTransformer<T>> createForDeserializedState() {
                    return Optional.empty();
                }

                @Override
                public Optional<StateSnapshotTransformer<byte[]>> createForSerializedState() {
                    return Optional.empty();
                }
            };
        }

        Optional<StateSnapshotTransformer<T>> createForDeserializedState();

        Optional<StateSnapshotTransformer<byte[]>> createForSerializedState();
    }

}
