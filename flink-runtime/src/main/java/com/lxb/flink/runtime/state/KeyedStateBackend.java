package com.lxb.flink.runtime.state;

import com.lxb.flink.api.common.state.State;
import com.lxb.flink.api.common.state.StateDescriptor;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.utl.Disposable;

import java.util.stream.Stream;

public interface KeyedStateBackend<K>
        extends KeyedStateFactory, PriorityQueueSetFactory, Disposable {
    void setCurrentKey(K newKey);

    K getCurrentKey();

    TypeSerializer<K> getKeySerializer();

    <N, S extends State, T> void applyToAllKeys(
            final N namespace,
            final TypeSerializer<N> namespaceSerializer,
            final StateDescriptor<S, T> stateDescriptor,
            final KeyedStateFunction<K, S> function) throws Exception;

    <N> Stream<K> getKeys(String state, N namespace);

    <N, S extends State, T> S getOrCreateKeyedState(
            TypeSerializer<N> namespaceSerializer,
            StateDescriptor<S, T> stateDescriptor) throws Exception;

    <N, S extends State> S getPartitionedState(
            N namespace,
            TypeSerializer<N> namespaceSerializer,
            StateDescriptor<S, ?> stateDescriptor) throws Exception;

    @Override
    void dispose();

    void registerKeySelectionListener(KeySelectionListener<K> listener);

    boolean deregisterKeySelectionListener(KeySelectionListener<K> listener);

    @FunctionalInterface
    interface KeySelectionListener<K> {
        void keySelected(K newKey);
    }

}
