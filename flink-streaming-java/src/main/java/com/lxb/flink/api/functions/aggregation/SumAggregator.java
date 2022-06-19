package com.lxb.flink.api.functions.aggregation;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.api.java.tupple.Tuple;
import com.lxb.flink.api.java.typeutils.TupleTypeInfo;
import com.lxb.flink.util.typeutils.FieldAccessor;
import com.lxb.flink.util.typeutils.FieldAccessorFactory;

public class SumAggregator<T> extends AggregationFunction<T> {
    private final FieldAccessor<T, Object> fieldAccessor;
    private final SumFunction              adder;
    private final TypeSerializer<T>        serializer;
    private final boolean                  isTuple;

    public SumAggregator(int pos, TypeInformation<T> typeInfo, ExecutionConfig config) {
        fieldAccessor = FieldAccessorFactory.getAccessor(typeInfo, pos, config);
        adder = SumFunction.getForClass(fieldAccessor.getFieldType().getTypeClass());
        if (typeInfo instanceof TupleTypeInfo) {
            isTuple = true;
            serializer = null;
        } else {
            isTuple = false;
            this.serializer = typeInfo.createSerializer(config);
        }
    }

    public SumAggregator(String field, TypeInformation<T> typeInfo, ExecutionConfig config) {
        fieldAccessor = FieldAccessorFactory.getAccessor(typeInfo, field, config);
        adder = SumFunction.getForClass(fieldAccessor.getFieldType().getTypeClass());
        if (typeInfo instanceof TupleTypeInfo) {
            isTuple = true;
            serializer = null;
        } else {
            isTuple = false;
            this.serializer = typeInfo.createSerializer(config);
        }
    }

    @Override
    public T reduce(T value1, T value2) throws Exception {
        if (isTuple) {
            Tuple result = ((Tuple) value1).copy();
            return fieldAccessor.set((T) result, adder.add(fieldAccessor.get(value1), fieldAccessor.get(value2)));
        } else {
            T result = serializer.copy(value1);
            return fieldAccessor.set(result, adder.add(fieldAccessor.get(value1), fieldAccessor.get(value2)));
        }
    }
}
