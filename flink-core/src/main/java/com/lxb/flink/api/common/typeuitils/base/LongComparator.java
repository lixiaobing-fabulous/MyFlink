package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public final class LongComparator extends BasicTypeComparator<Long> {
    public LongComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException {
        long l1   = firstSource.readLong();
        long l2   = secondSource.readLong();
        int  comp = (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public boolean supportsNormalizedKey() {
        return true;
    }

    @Override
    public int getNormalizeKeyLen() {
        return 8;
    }

    @Override
    public boolean isNormalizedKeyPrefixOnly(int keyBytes) {
        return keyBytes < 8;
    }

    @Override
    public void putNormalizedKey(Long lValue, MemorySegment target, int offset, int numBytes) {
        NormalizedKeyUtil.putLongNormalizedKey(lValue, target, offset, numBytes);
    }

    @Override
    public LongComparator duplicate() {
        return new LongComparator(ascendingComparison);
    }

}
