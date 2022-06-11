package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public interface CopyableValue<T> extends Value {
    int getBinaryLength();

    void copyTo(T target);

    T copy();

    void copy(DataInputView source, DataOutputView target) throws IOException;

}
