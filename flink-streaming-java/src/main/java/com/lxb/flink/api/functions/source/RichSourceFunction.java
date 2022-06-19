package com.lxb.flink.api.functions.source;

import com.lxb.flink.api.common.functions.AbstractRichFunction;

public abstract class RichSourceFunction<OUT> extends AbstractRichFunction implements SourceFunction<OUT> {
}
