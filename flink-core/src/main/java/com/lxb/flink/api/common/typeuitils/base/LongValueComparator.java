package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.api.common.typeuitils.TypeComparator;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;
import com.lxb.flink.types.LongValue;
import com.lxb.flink.types.NormalizableKey;

import java.io.IOException;

public class LongValueComparator extends TypeComparator<LongValue> {
    private final boolean ascendingComparison;

    private final LongValue reference = new LongValue();

    private final LongValue tempReference = new LongValue();

    private final TypeComparator<?>[] comparators = new TypeComparator[] {this};

    public LongValueComparator(boolean ascending) {
        this.ascendingComparison = ascending;
    }

    @Override
    public int hash(LongValue record) {
        return record.hashCode();
    }

    @Override
    public void setReference(LongValue toCompare) {
        toCompare.copyTo(reference);
    }

    @Override
    public boolean equalToReference(LongValue candidate) {
        return candidate.equals(this.reference);
    }

    @Override
    public int compareToReference(TypeComparator<LongValue> referencedComparator) {
        LongValue otherRef = ((LongValueComparator) referencedComparator).reference;
        int comp = otherRef.compareTo(reference);
        return ascendingComparison ? comp : -comp;
    }

    @Override
    public int compare(LongValue first, LongValue second) {
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
        return NormalizableKey.class.isAssignableFrom(LongValue.class);
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
    public void putNormalizedKey(LongValue record, MemorySegment target, int offset, int numBytes) {
        record.copyNormalizedKey(target, offset, numBytes);
    }

    @Override
    public boolean invertNormalizedKey() {
        return !ascendingComparison;
    }

    @Override
    public TypeComparator<LongValue> duplicate() {
        return new LongValueComparator(ascendingComparison);
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
    public void writeWithKeyNormalization(LongValue record, DataOutputView target) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongValue readWithKeyDenormalization(LongValue reuse, DataInputView source) throws IOException {
        throw new UnsupportedOperationException();
    }

}
