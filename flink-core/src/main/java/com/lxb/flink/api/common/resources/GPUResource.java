package com.lxb.flink.api.common.resources;

import java.math.BigDecimal;

public class GPUResource extends Resource {
    public static final String NAME = "GPU";

    public GPUResource(double value) {
        super(NAME, value);
    }

    private GPUResource(BigDecimal value) {
        super(NAME, value);
    }

    @Override
    public Resource create(BigDecimal value) {
        return new GPUResource(value);
    }

}
