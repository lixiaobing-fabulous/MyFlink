package com.lxb.flink.api.operators;

import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.core.fs.CloseableRegistry;
import com.lxb.flink.runtime.jobgraph.OperatorID;
import com.lxb.flink.runtime.tasks.ProcessingTimeService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface StreamTaskStateInitializer {
    StreamOperatorStateContext streamOperatorStateContext(
            @Nonnull OperatorID operatorID,
            @Nonnull String operatorClassName,
            @Nonnull ProcessingTimeService processingTimeService,
            @Nonnull KeyContext keyContext,
            @Nullable TypeSerializer<?> keySerializer,
            @Nonnull CloseableRegistry streamTaskCloseableRegistry) throws Exception;

}
