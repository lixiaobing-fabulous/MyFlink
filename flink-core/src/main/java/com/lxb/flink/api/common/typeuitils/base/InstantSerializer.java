package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;
import java.time.Instant;

public class InstantSerializer extends TypeSerializerSingleton<Instant> {
    static final         int               SECONDS_BYTES = Long.BYTES;
    static final         int               NANOS_BYTES   = Integer.BYTES;
    private static final long              NULL_SECONDS  = Long.MIN_VALUE;
    private static final int               NULL_NANOS    = Integer.MIN_VALUE;
    public static final  InstantSerializer INSTANCE      = new InstantSerializer();

    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public Instant createInstance() {
        return Instant.EPOCH;
    }

    @Override
    public Instant copy(Instant from) {
        return from;
    }

    @Override
    public Instant copy(Instant from, Instant reuse) {
        return from;
    }

    @Override
    public int getLength() {
        return SECONDS_BYTES + NANOS_BYTES;
    }

    @Override
    public void serialize(Instant record, DataOutputView target) throws IOException {
        if (record == null) {
            target.writeLong(NULL_SECONDS);
            target.writeInt(NULL_NANOS);
        } else {
            target.writeLong(record.getEpochSecond());
            target.writeInt(record.getNano());
        }
    }

    @Override
    public Instant deserialize(DataInputView source) throws IOException {
        final long seconds = source.readLong();
        final int  nanos   = source.readInt();
        if (seconds == NULL_SECONDS && nanos == NULL_NANOS) {
            return null;
        }
        return Instant.ofEpochSecond(seconds, nanos);
    }

    @Override
    public Instant deserialize(Instant reuse, DataInputView source) throws IOException {
        return deserialize(source);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeLong(source.readLong());
        target.writeInt(source.readInt());
    }
}
