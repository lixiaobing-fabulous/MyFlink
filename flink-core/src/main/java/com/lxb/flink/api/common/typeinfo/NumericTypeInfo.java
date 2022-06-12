package com.lxb.flink.api.common.typeinfo;

import com.lxb.flink.api.common.typeuitils.TypeComparator;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;

import java.util.Arrays;
import java.util.HashSet;

import static com.lxb.flink.utl.Preconditions.checkArgument;

public abstract class NumericTypeInfo<T> extends BasicTypeInfo<T> {
    private static final HashSet<Class<?>> numericalTypes = new HashSet<>(
            Arrays.asList(
                    Integer.class,
                    Long.class,
                    Double.class,
                    Byte.class,
                    Short.class,
                    Float.class,
                    Character.class));
    protected NumericTypeInfo(Class<T> clazz, Class<?>[] possibleCastTargetTypes, TypeSerializer<T> serializer, Class<? extends
            TypeComparator<T>> comparatorClass) {
        super(clazz, possibleCastTargetTypes, serializer, comparatorClass);

        checkArgument(numericalTypes.contains(clazz),
                "The given class %s is not a numerical type", clazz.getSimpleName());
    }

}
