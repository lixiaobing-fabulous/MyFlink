package com.lxb.flink.api.common.typeinfo;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeuitils.TypeComparator;

public interface AtomicType<T> {
    TypeComparator<T> createComparator(boolean sortOrderAscending, ExecutionConfig executionConfig);

}
