package com.lxb.flink.api.functions.sink;

import com.lxb.flink.api.common.functions.AbstractRichFunction;

public abstract class RichSinkFunction<IN> extends AbstractRichFunction implements SinkFunction<IN> {
}
