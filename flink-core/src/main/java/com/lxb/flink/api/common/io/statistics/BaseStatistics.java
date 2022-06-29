package com.lxb.flink.api.common.io.statistics;

import com.lxb.flink.annotation.PublicEvolving;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-29
 */
public interface BaseStatistics {
    /**
     * Constant indicating that the input size is unknown.
     */
    @PublicEvolving
    public static final long SIZE_UNKNOWN = -1;

    /**
     * Constant indicating that the number of records is unknown;
     */
    @PublicEvolving
    public static final long NUM_RECORDS_UNKNOWN = -1;

    /**
     * Constant indicating that average record width is unknown.
     */
    @PublicEvolving
    public static final float AVG_RECORD_BYTES_UNKNOWN = -1.0f;

    // --------------------------------------------------------------------------------------------

    /**
     * Gets the total size of the input.
     *
     * @return The total size of the input, in bytes.
     */
    @PublicEvolving
    public long getTotalInputSize();

    /**
     * Gets the number of records in the input (= base cardinality).
     *
     * @return The number of records in the input.
     */
    @PublicEvolving
    public long getNumberOfRecords();

    /**
     * Gets the average width of a record, in bytes.
     *
     * @return The average width of a record in bytes.
     */
    @PublicEvolving
    public float getAverageRecordWidth();
}
