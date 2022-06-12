package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public final class IntComparator extends BasicTypeComparator<Integer> {
    public IntComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException {
        int i1 = firstSource.readInt();
        int i2 = secondSource.readInt();
        int comp = (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public boolean supportsNormalizedKey() {
        return true;
    }

    @Override
    public int getNormalizeKeyLen() {
        return 4;
    }

    @Override
    public boolean isNormalizedKeyPrefixOnly(int keyBytes) {
        return keyBytes < 4;
    }

    @Override
    public void putNormalizedKey(Integer iValue, MemorySegment target, int offset, int numBytes) {
        NormalizedKeyUtil.putIntNormalizedKey(iValue, target, offset, numBytes);
    }

    @Override
    public IntComparator duplicate() {
        return new IntComparator(ascendingComparison);
    }

}
