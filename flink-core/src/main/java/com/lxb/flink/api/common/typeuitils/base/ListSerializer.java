package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

public final class ListSerializer<T> extends TypeSerializer<List<T>> {
    private final TypeSerializer<T> elementSerializer;
    public ListSerializer(TypeSerializer<T> elementSerializer) {
        this.elementSerializer = checkNotNull(elementSerializer);
    }
    public TypeSerializer<T> getElementSerializer() {
        return elementSerializer;
    }
    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public TypeSerializer<List<T>> duplicate() {
        TypeSerializer<T> duplicateElement = elementSerializer.duplicate();
        return duplicateElement == elementSerializer ? this : new ListSerializer<>(duplicateElement);
    }
    @Override
    public List<T> createInstance() {
        return new ArrayList<>(0);
    }

    @Override
    public List<T> copy(List<T> from) {
        List<T> newList = new ArrayList<>(from.size());
        for (T element : from) {
            newList.add(elementSerializer.copy(element));
        }
        return newList;
    }
    @Override
    public List<T> copy(List<T> from, List<T> reuse) {
        return copy(from);
    }

    @Override
    public int getLength() {
        return -1; // var length
    }
    @Override
    public void serialize(List<T> list, DataOutputView target) throws IOException {
        final int size = list.size();
        target.writeInt(size);
        for (T element : list) {
            elementSerializer.serialize(element, target);
        }
    }
    @Override
    public List<T> deserialize(DataInputView source) throws IOException {
        final int size = source.readInt();
        // create new list with (size + 1) capacity to prevent expensive growth when a single element is added
        final List<T> list = new ArrayList<>(size + 1);
        for (int i = 0; i < size; i++) {
            list.add(elementSerializer.deserialize(source));
        }
        return list;
    }
    @Override
    public List<T> deserialize(List<T> reuse, DataInputView source) throws IOException {
        return deserialize(source);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        // copy number of elements
        final int num = source.readInt();
        target.writeInt(num);
        for (int i = 0; i < num; i++) {
            elementSerializer.copy(source, target);
        }
    }
    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj != null && obj.getClass() == getClass() &&
                        elementSerializer.equals(((ListSerializer<?>) obj).elementSerializer));
    }

    @Override
    public int hashCode() {
        return elementSerializer.hashCode();
    }

}
