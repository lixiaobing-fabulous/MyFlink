package com.lxb.flink.runtime.jobgraph;

public enum ScheduleMode {
    LAZY_FROM_SOURCES(true),

    LAZY_FROM_SOURCES_WITH_BATCH_SLOT_REQUEST(true),

    EAGER(false);

    private final boolean allowLazyDeployment;

    ScheduleMode(boolean allowLazyDeployment) {
        this.allowLazyDeployment = allowLazyDeployment;
    }

    public boolean allowLazyDeployment() {
        return allowLazyDeployment;
    }

}
