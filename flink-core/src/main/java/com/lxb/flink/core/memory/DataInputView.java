package com.lxb.flink.core.memory;

import java.io.DataInput;
import java.io.IOException;

public interface DataInputView extends DataInput {

    void skipBytesToRead(int numBytes) throws IOException;

    int read(byte[] b, int off, int len) throws IOException;

    int read(byte[] b) throws IOException;

}
