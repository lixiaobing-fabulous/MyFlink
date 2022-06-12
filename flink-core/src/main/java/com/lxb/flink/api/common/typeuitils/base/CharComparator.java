package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public class CharComparator extends BasicTypeComparator<Character> {
    public CharComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException {
        char c1   = firstSource.readChar();
        char c2   = secondSource.readChar();
        int  comp = (c1 < c2 ? -1 : (c1 == c2 ? 0 : 1));
        return ascendingComparison ? comp : -comp;
    }
    @Override
    public boolean supportsNormalizedKey() {
        return true;
    }

    @Override
    public int getNormalizeKeyLen() {
        return 2;
    }

    @Override
    public boolean isNormalizedKeyPrefixOnly(int keyBytes) {
        return keyBytes < 2;
    }

    @Override
    public void putNormalizedKey(Character value, MemorySegment target, int offset, int numBytes) {
        NormalizedKeyUtil.putCharNormalizedKey(value, target, offset, numBytes);
    }

    @Override
    public CharComparator duplicate() {
        return new CharComparator(ascendingComparison);
    }

}
