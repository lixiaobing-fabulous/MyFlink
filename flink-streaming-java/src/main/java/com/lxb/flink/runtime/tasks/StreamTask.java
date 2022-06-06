package com.lxb.flink.runtime.tasks;

import com.lxb.flink.api.operators.StreamOperator;
import com.lxb.flink.runtime.jobgraph.tasks.AbstractInvokable;

public class StreamTask<OUT, OP extends StreamOperator<OUT>> extends AbstractInvokable {
}
