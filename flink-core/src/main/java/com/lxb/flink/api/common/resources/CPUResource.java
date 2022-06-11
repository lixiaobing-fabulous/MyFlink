package com.lxb.flink.api.common.resources;

import java.math.BigDecimal;

public class CPUResource extends Resource {
    public static final String NAME = "CPU";

    public CPUResource(double value) {
        super(NAME, value);
    }

    private CPUResource(BigDecimal value) {
        super(NAME, value);
    }

    @Override
    public Resource create(BigDecimal value) {
        return new CPUResource(value);
    }

}
