package com.lxb.flink.api.functions.aggregation;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.util.typeutils.FieldAccessor;
import com.lxb.flink.util.typeutils.FieldAccessorFactory;

public class ComparableAggregator<T> extends AggregationFunction<T> {
    private       Comparator               comparator;
    private       boolean                  byAggregate;
    private       boolean                  first;
    private final FieldAccessor<T, Object> fieldAccessor;

    private ComparableAggregator(AggregationType aggregationType, FieldAccessor<T, Object> fieldAccessor, boolean first) {
        this.comparator = Comparator.getForAggregation(aggregationType);
        this.byAggregate = (aggregationType == AggregationType.MAXBY) || (aggregationType == AggregationType.MINBY);
        this.first = first;
        this.fieldAccessor = fieldAccessor;
    }

    public ComparableAggregator(int positionToAggregate,
                                TypeInformation<T> typeInfo,
                                AggregationType aggregationType,
                                ExecutionConfig config) {
        this(positionToAggregate, typeInfo, aggregationType, false, config);
    }

    public ComparableAggregator(int positionToAggregate,
                                TypeInformation<T> typeInfo,
                                AggregationType aggregationType,
                                boolean first,
                                ExecutionConfig config) {
        this(aggregationType, FieldAccessorFactory.getAccessor(typeInfo, positionToAggregate, config), first);
    }

    public ComparableAggregator(String field,
                                TypeInformation<T> typeInfo,
                                AggregationType aggregationType,
                                boolean first,
                                ExecutionConfig config) {
        this(aggregationType, FieldAccessorFactory.getAccessor(typeInfo, field, config), first);
    }

    @Override
    public T reduce(T value1, T value2) throws Exception {
        Comparable<Object> o1 = (Comparable<Object>) fieldAccessor.get(value1);
        Object             o2 = fieldAccessor.get(value2);

        int c = comparator.isExtremal(o1, o2);

        if (byAggregate) {
            // if they are the same we choose based on whether we want to first or last
            // element with the min/max.
            if (c == 0) {
                return first ? value1 : value2;
            }

            return c == 1 ? value1 : value2;

        } else {
            if (c == 0) {
                value1 = fieldAccessor.set(value1, o2);
            }
            return value1;
        }
    }
}
