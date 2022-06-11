package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.utl.ReflectionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class ListValue<V extends Value> implements Value, List<V> {
    private final Class<V> valueClass;
    private final List<V>  list;

    public ListValue() {
        this.valueClass = ReflectionUtil.<V>getTemplateType1(this.getClass());

        this.list = new ArrayList<V>();
    }

    public ListValue(final Collection<V> c) {
        this.valueClass = ReflectionUtil.<V>getTemplateType1(this.getClass());

        this.list = new ArrayList<V>(c);
    }

    @Override
    public Iterator<V> iterator() {
        return this.list.iterator();
    }

    @Override
    public void read(final DataInputView in) throws IOException {
        int size = in.readInt();
        this.list.clear();

        try {
            for (; size > 0; size--) {
                final V val = this.valueClass.newInstance();
                val.read(in);

                this.list.add(val);
            }
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(final DataOutputView out) throws IOException {
        out.writeInt(this.list.size());
        for (final V value : this.list) {
            value.write(out);
        }
    }

    @Override
    public int hashCode() {
        final int prime  = 41;
        int       result = 1;
        result = prime * result + (this.list == null ? 0 : this.list.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ListValue<?> other = (ListValue<?>) obj;
        if (this.list == null) {
            if (other.list != null) {
                return false;
            }
        } else if (!this.list.equals(other.list)) {
            return false;
        }
        return true;
    }

    @Override
    public void add(final int index, final V element) {
        this.list.add(index, element);
    }

    @Override
    public boolean add(final V e) {
        return this.list.add(e);
    }

    @Override
    public boolean addAll(final Collection<? extends V> c) {
        return this.list.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends V> c) {
        return this.list.addAll(index, c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return this.list.contains(o);
    }

    @Override
    public String toString() {
        return this.list.toString();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public V get(final int index) {
        return this.list.get(index);
    }

    @Override
    public int indexOf(final Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<V> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<V> listIterator(final int index) {
        return this.list.listIterator(index);
    }

    @Override
    public V remove(final int index) {
        return this.list.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public V set(final int index, final V element) {
        return this.list.set(index, element);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<V> subList(final int fromIndex, final int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return this.list.toArray(a);
    }

}
