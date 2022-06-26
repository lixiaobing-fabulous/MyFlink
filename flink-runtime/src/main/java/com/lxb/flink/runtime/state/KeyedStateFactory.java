package com.lxb.flink.runtime.state;

import com.lxb.flink.api.common.state.State;
import com.lxb.flink.api.common.state.StateDescriptor;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;

import javax.annotation.Nonnull;

public interface KeyedStateFactory {
    @Nonnull
    default <N, SV, S extends State, IS extends S> IS createInternalState(
            @Nonnull TypeSerializer<N> namespaceSerializer,
            @Nonnull StateDescriptor<S, SV> stateDesc) throws Exception {
        return createInternalState(namespaceSerializer, stateDesc, StateSnapshotTransformer.StateSnapshotTransformFactory.noTransform());
    }

    @Nonnull
    <N, SV, SEV, S extends State, IS extends S> IS createInternalState(
            @Nonnull TypeSerializer<N> namespaceSerializer,
            @Nonnull StateDescriptor<S, SV> stateDesc,
            @Nonnull StateSnapshotTransformer.StateSnapshotTransformFactory<SEV> snapshotTransformFactory) throws Exception;

}
