package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.api.common.typeuitils.TypeComparator;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;
import com.lxb.flink.types.IntValue;
import com.lxb.flink.types.NormalizableKey;

import java.io.IOException;

public class IntValueComparator extends TypeComparator<IntValue> {
    private final boolean ascendingComparison;

    private final IntValue reference = new IntValue();

    private final IntValue tempReference = new IntValue();

    private final TypeComparator<?>[] comparators = new TypeComparator[]{this};

    public IntValueComparator(boolean ascending) {
        this.ascendingComparison = ascending;
    }

    @Override
    public int hash(IntValue record) {
        return record.hashCode();
    }

    @Override
    public void setReference(IntValue toCompare) {
        toCompare.copyTo(reference);
    }

    @Override
    public boolean equalToReference(IntValue candidate) {
        return candidate.equals(this.reference);
    }

    @Override
    public int compareToReference(TypeComparator<IntValue> referencedComparator) {
        IntValue otherRef = ((IntValueComparator) referencedComparator).reference;
        int      comp     = otherRef.compareTo(reference);
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public int compare(IntValue first, IntValue second) {
        int comp = first.compareTo(second);
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException {
        reference.read(firstSource);
        tempReference.read(secondSource);
        int comp = reference.compareTo(tempReference);
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public boolean supportsNormalizedKey() {
        return NormalizableKey.class.isAssignableFrom(IntValue.class);
    }

    @Override
    public int getNormalizeKeyLen() {
        return reference.getMaxNormalizedKeyLen();
    }

    @Override
    public boolean isNormalizedKeyPrefixOnly(int keyBytes) {
        return keyBytes < getNormalizeKeyLen();
    }

    @Override
    public void putNormalizedKey(IntValue record, MemorySegment target, int offset, int numBytes) {
        record.copyNormalizedKey(target, offset, numBytes);
    }

    @Override
    public boolean invertNormalizedKey() {
        return !ascendingComparison;
    }

    @Override
    public TypeComparator<IntValue> duplicate() {
        return new IntValueComparator(ascendingComparison);
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
    public void writeWithKeyNormalization(IntValue record, DataOutputView target) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntValue readWithKeyDenormalization(IntValue reuse, DataInputView source) throws IOException {
        throw new UnsupportedOperationException();
    }
}
