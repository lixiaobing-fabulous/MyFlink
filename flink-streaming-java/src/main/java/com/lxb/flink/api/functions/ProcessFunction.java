package com.lxb.flink.api.functions;

import com.lxb.flink.api.TimeDomain;
import com.lxb.flink.api.TimerService;
import com.lxb.flink.api.common.functions.AbstractRichFunction;
import com.lxb.flink.utl.Collector;
import com.lxb.flink.utl.OutputTag;

public abstract class ProcessFunction<I, O> extends AbstractRichFunction {
    public abstract void processElement(I value, Context ctx, Collector<O> out) throws Exception;

    public void onTimer(long timestamp, OnTimerContext ctx, Collector<O> out) throws Exception {
    }

    public abstract class Context {
        public abstract Long timestamp();

        public abstract TimerService timerService();

        public abstract <X> void output(OutputTag<X> outputTag, X value);

    }

    public abstract class OnTimerContext extends Context {
        public abstract TimeDomain timeDomain();

    }

}
