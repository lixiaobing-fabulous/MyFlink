package com.lxb.flink.api.common.typeuitils;

import java.util.LinkedList;
import java.util.List;

public abstract class CompositeTypeComparator<T> extends TypeComparator<T> {
    @Override
    public TypeComparator[] getFlatComparators() {
        List<TypeComparator> flatComparators = new LinkedList<TypeComparator>();
        this.getFlatComparator(flatComparators);
        return flatComparators.toArray(new TypeComparator[flatComparators.size()]);
    }

    public abstract void getFlatComparator(List<TypeComparator> flatComparators);

}
