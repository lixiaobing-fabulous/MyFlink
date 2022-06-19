package com.lxb.flink.api.functions.sink;

import com.lxb.flink.api.common.functions.util.PrintSinkOutputWriter;
import com.lxb.flink.api.operators.StreamingRuntimeContext;
import com.lxb.flink.configuration.Configuration;

public class PrintSinkFunction<IN> extends RichSinkFunction<IN> {
    private final PrintSinkOutputWriter<IN> writer;

    public PrintSinkFunction() {
        writer = new PrintSinkOutputWriter<>(false);
    }

    public PrintSinkFunction(final boolean stdErr) {
        writer = new PrintSinkOutputWriter<>(stdErr);
    }

    public PrintSinkFunction(final String sinkIdentifier, final boolean stdErr) {
        writer = new PrintSinkOutputWriter<>(sinkIdentifier, stdErr);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        StreamingRuntimeContext context = (StreamingRuntimeContext) getRuntimeContext();
        writer.open(context.getIndexOfThisSubtask(), context.getNumberOfParallelSubtasks());
    }

    @Override
    public void invoke(IN record) {
        writer.write(record);
    }

    @Override
    public String toString() {
        return writer.toString();
    }

}
