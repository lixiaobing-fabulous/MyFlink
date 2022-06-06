package com.lxb.flink.api.common;

public class ExecutionConfig {
    public static final int PARALLELISM_DEFAULT = -1;
    public static final int PARALLELISM_UNKNOWN = -2;

    private int parallelism = 1;
    public int getParallelism() {
        return parallelism;
    }
    public ExecutionConfig setParallelism(int parallelism) {
        if (parallelism != PARALLELISM_UNKNOWN) {
            if (parallelism < 1 && parallelism != PARALLELISM_DEFAULT) {
                throw new IllegalArgumentException(
                        "Parallelism must be at least one, or ExecutionConfig.PARALLELISM_DEFAULT (use system default).");
            }
            this.parallelism = parallelism;
        }
        return this;
    }

}
