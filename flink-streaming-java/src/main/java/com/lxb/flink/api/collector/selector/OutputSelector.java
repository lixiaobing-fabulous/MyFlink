package com.lxb.flink.api.collector.selector;

import java.io.Serializable;

public interface OutputSelector<OUT> extends Serializable {
    Iterable<String> select(OUT value);
}
