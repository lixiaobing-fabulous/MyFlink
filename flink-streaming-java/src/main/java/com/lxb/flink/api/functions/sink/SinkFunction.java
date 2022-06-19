package com.lxb.flink.api.functions.sink;

import com.lxb.flink.annotation.Public;
import com.lxb.flink.api.common.functions.Function;

import java.io.Serializable;

public interface SinkFunction<IN> extends Function, Serializable {
    @Deprecated
    default void invoke(IN value) throws Exception {
    }

    default void invoke(IN value, Context context) throws Exception {
        invoke(value);
    }

    @Public
    interface Context<T> {

        long currentProcessingTime();

        long currentWatermark();

        Long timestamp();
    }

}
