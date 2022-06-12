package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.api.common.typeuitils.TypeComparator;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;
import com.lxb.flink.types.NormalizableKey;
import com.lxb.flink.types.NullValue;

import java.io.IOException;

public class NullValueComparator extends TypeComparator<NullValue> {
    private final TypeComparator<?>[] comparators = new TypeComparator[]{this};

    private final static NullValueComparator INSTANCE = new NullValueComparator();

    public static NullValueComparator getInstance() {
        return INSTANCE;
    }

    private NullValueComparator() {
    }

    @Override
    public int hash(NullValue record) {
        return record.hashCode();
    }

    @Override
    public void setReference(NullValue toCompare) {
    }

    @Override
    public boolean equalToReference(NullValue candidate) {
        return true;
    }

    @Override
    public int compareToReference(TypeComparator<NullValue> referencedComparator) {
        return 0;
    }

    @Override
    public int compare(NullValue first, NullValue second) {
        return 0;
    }

    @Override
    public int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException {
        return 0;
    }

    @Override
    public boolean supportsNormalizedKey() {
        return NormalizableKey.class.isAssignableFrom(NullValue.class);
    }

    @Override
    public int getNormalizeKeyLen() {
        return NullValue.getInstance().getMaxNormalizedKeyLen();
    }

    @Override
    public boolean isNormalizedKeyPrefixOnly(int keyBytes) {
        return keyBytes < getNormalizeKeyLen();
    }

    @Override
    public void putNormalizedKey(NullValue record, MemorySegment target, int offset, int numBytes) {
        record.copyNormalizedKey(target, offset, numBytes);
    }

    @Override
    public boolean invertNormalizedKey() {
        return false;
    }

    @Override
    public TypeComparator<NullValue> duplicate() {
        return NullValueComparator.getInstance();
    }

    @Override
    public int extractKeys(Object record, Object[] target, int index) {
        target[index] = record;
        return 1;
    }

    @Override
    public TypeComparator<?>[] getFlatComparators() {
        return comparators;
    }

    @Override
    public boolean supportsSerializationWithKeyNormalization() {
        return false;
    }

    @Override
    public void writeWithKeyNormalization(NullValue record, DataOutputView target) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NullValue readWithKeyDenormalization(NullValue reuse, DataInputView source) throws IOException {
        throw new UnsupportedOperationException();
    }
}
