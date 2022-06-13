package com.lxb.flink.api;

public interface TimerService {
    String UNSUPPORTED_REGISTER_TIMER_MSG = "Setting timers is only supported on a keyed streams.";
    String UNSUPPORTED_DELETE_TIMER_MSG   = "Deleting timers is only supported on a keyed streams.";

    long currentProcessingTime();

    long currentWatermark();

    void registerProcessingTimeTimer(long time);

    void registerEventTimeTimer(long time);

    void deleteProcessingTimeTimer(long time);

    void deleteEventTimeTimer(long time);

}
