package com.lxb.flink.runtime.tasks;

import com.lxb.flink.api.operators.OneInputStreamOperator;

public class OneInputStreamTask<IN, OUT> extends StreamTask<OUT, OneInputStreamOperator<IN, OUT>> {
}
