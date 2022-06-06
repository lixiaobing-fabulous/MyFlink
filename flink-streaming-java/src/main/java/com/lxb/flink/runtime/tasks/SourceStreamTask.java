package com.lxb.flink.runtime.tasks;

import com.lxb.flink.api.functions.source.SourceFunction;
import com.lxb.flink.api.operators.StreamSource;

public class SourceStreamTask<OUT, SRC extends SourceFunction<OUT>, OP extends StreamSource<OUT, SRC>> extends StreamTask<OUT, OP> {

}
