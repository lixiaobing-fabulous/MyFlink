package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public final class BooleanComparator extends BasicTypeComparator<Boolean> {
    public BooleanComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException {
        final int fs   = firstSource.readBoolean() ? 1 : 0;
        final int ss   = secondSource.readBoolean() ? 1 : 0;
        int       comp = fs - ss;
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public boolean supportsNormalizedKey() {
        return true;
    }

    @Override
    public int getNormalizeKeyLen() {
        return 1;
    }

    @Override
    public boolean isNormalizedKeyPrefixOnly(int keyBytes) {
        return keyBytes < 1;
    }

    @Override
    public void putNormalizedKey(Boolean value, MemorySegment target, int offset, int numBytes) {
        NormalizedKeyUtil.putBooleanNormalizedKey(value, target, offset, numBytes);
    }

    @Override
    public BooleanComparator duplicate() {
        return new BooleanComparator(ascendingComparison);
    }

}
