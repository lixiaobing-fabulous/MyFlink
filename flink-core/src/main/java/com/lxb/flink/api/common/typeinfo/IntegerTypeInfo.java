package com.lxb.flink.api.common.typeinfo;

import com.lxb.flink.api.common.typeuitils.TypeComparator;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;

import java.util.Arrays;
import java.util.HashSet;

import static com.lxb.flink.utl.Preconditions.checkArgument;

public class IntegerTypeInfo<T> extends NumericTypeInfo<T> {
    private static final HashSet<Class<?>> integerTypes = new HashSet<>(
            Arrays.asList(
                    Integer.class,
                    Long.class,
                    Byte.class,
                    Short.class,
                    Character.class));

    protected IntegerTypeInfo(Class<T> clazz, Class<?>[] possibleCastTargetTypes, TypeSerializer<T> serializer, Class<? extends TypeComparator<T>> comparatorClass) {
        super(clazz, possibleCastTargetTypes, serializer, comparatorClass);

        checkArgument(integerTypes.contains(clazz),
                "The given class %s is not a integer type.", clazz.getSimpleName());
    }

}
