package com.lxb.flink.runtime.state.internal;

import com.lxb.flink.api.common.state.State;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.runtime.state.StateEntry;

import java.util.Collection;

public interface InternalKvState<K, N, V> extends State {
    TypeSerializer<K> getKeySerializer();

    TypeSerializer<N> getNamespaceSerializer();

    TypeSerializer<V> getValueSerializer();

    void setCurrentNamespace(N namespace);

    byte[] getSerializedValue(
            final byte[] serializedKeyAndNamespace,
            final TypeSerializer<K> safeKeySerializer,
            final TypeSerializer<N> safeNamespaceSerializer,
            final TypeSerializer<V> safeValueSerializer) throws Exception;

    StateIncrementalVisitor<K, N, V> getStateIncrementalVisitor(int recommendedMaxNumberOfReturnedRecords);

    interface StateIncrementalVisitor<K, N, V> {
        boolean hasNext();

        Collection<StateEntry<K, N, V>> nextEntries();

        void remove(StateEntry<K, N, V> stateEntry);

        void update(StateEntry<K, N, V> stateEntry, V newValue);

    }
}
