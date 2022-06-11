package com.lxb.flink.api.common.typeuitils;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;
import java.io.Serializable;

public abstract class TypeComparator<T> implements Serializable {

    public abstract int hash(T record);

    public abstract void setReference(T toCompare);

    public abstract boolean equalToReference(T candidate);

    public abstract int compareToReference(TypeComparator<T> referencedComparator);

    public boolean supportsCompareAgainstReference() {
        return false;
    }

    public abstract int compare(T first, T second);

    public abstract int compareSerialized(DataInputView firstSource, DataInputView secondSource) throws IOException;

    public abstract boolean supportsNormalizedKey();

    public abstract boolean supportsSerializationWithKeyNormalization();

    public abstract int getNormalizeKeyLen();

    public abstract boolean isNormalizedKeyPrefixOnly(int keyBytes);

    public abstract void putNormalizedKey(T record, MemorySegment target, int offset, int numBytes);


    public abstract void writeWithKeyNormalization(T record, DataOutputView target) throws IOException;

    public abstract T readWithKeyDenormalization(T reuse, DataInputView source) throws IOException;

    public abstract boolean invertNormalizedKey();

    public abstract TypeComparator<T> duplicate();

    public abstract int extractKeys(Object record, Object[] target, int index);

    public abstract TypeComparator[] getFlatComparators();

    public int compareAgainstReference(Comparable[] keys) {
        throw new UnsupportedOperationException("Workaround hack.");
    }


}
