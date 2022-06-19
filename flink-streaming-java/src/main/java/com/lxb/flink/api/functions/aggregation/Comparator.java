package com.lxb.flink.api.functions.aggregation;

import java.io.Serializable;

public abstract class Comparator implements Serializable {
    public abstract <R> int isExtremal(Comparable<R> o1, R o2);

    public static Comparator getForAggregation(AggregationFunction.AggregationType type) {
        switch (type) {
            case MAX:
                return new MaxComparator();
            case MIN:
                return new MinComparator();
            case MINBY:
                return new MinByComparator();
            case MAXBY:
                return new MaxByComparator();
            default:
                throw new IllegalArgumentException("Unsupported aggregation type.");
        }
    }

    private static class MaxComparator extends Comparator {

        private static final long serialVersionUID = 1L;

        @Override
        public <R> int isExtremal(Comparable<R> o1, R o2) {
            return o1.compareTo(o2) > 0 ? 1 : 0;
        }

    }

    private static class MaxByComparator extends Comparator {

        private static final long serialVersionUID = 1L;

        @Override
        public <R> int isExtremal(Comparable<R> o1, R o2) {
            int c = o1.compareTo(o2);
            if (c > 0) {
                return 1;
            }
            if (c == 0) {
                return 0;
            } else {
                return -1;
            }
        }

    }

    private static class MinByComparator extends Comparator {

        private static final long serialVersionUID = 1L;

        @Override
        public <R> int isExtremal(Comparable<R> o1, R o2) {
            int c = o1.compareTo(o2);
            if (c < 0) {
                return 1;
            }
            if (c == 0) {
                return 0;
            } else {
                return -1;
            }
        }

    }

    private static class MinComparator extends Comparator {

        private static final long serialVersionUID = 1L;

        @Override
        public <R> int isExtremal(Comparable<R> o1, R o2) {
            return o1.compareTo(o2) < 0 ? 1 : 0;
        }

    }
}
