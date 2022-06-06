package com.lxb.flink.core.io;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public interface IOReadableWritable {

    void write(DataOutputView out) throws IOException;

    void read(DataInputView in) throws IOException;

}
