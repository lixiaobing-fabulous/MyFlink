package com.lxb.flink.api.operators;

import com.lxb.flink.api.common.functions.Function;

import static java.util.Objects.requireNonNull;

public class AbstractUdfStreamOperator<OUT, F extends Function> extends AbstractStreamOperator<OUT> {

    protected final F userFunction;

    public AbstractUdfStreamOperator(F userFunction) {
        this.userFunction = requireNonNull(userFunction);
    }

}
