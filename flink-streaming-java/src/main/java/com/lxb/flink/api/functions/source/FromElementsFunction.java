package com.lxb.flink.api.functions.source;

import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataInputViewStreamWrapper;
import com.lxb.flink.core.memory.DataOutputViewStreamWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FromElementsFunction<T> implements SourceFunction<T> {
    private final TypeSerializer<T> serializer;

    private final byte[] elementsSerialized;

    private final int numElements;

    private volatile int numElementsEmitted;

    private volatile int numElementsToSkip;

    private volatile boolean isRunning = true;

    public FromElementsFunction(TypeSerializer<T> serializer, Iterable<T> elements) throws IOException {
        ByteArrayOutputStream       baos    = new ByteArrayOutputStream();
        DataOutputViewStreamWrapper wrapper = new DataOutputViewStreamWrapper(baos);

        int count = 0;
        try {
            for (T element : elements) {
                serializer.serialize(element, wrapper);
                count++;
            }
        } catch (Exception e) {
            throw new IOException("Serializing the source elements failed: " + e.getMessage(), e);
        }

        this.serializer = serializer;
        this.elementsSerialized = baos.toByteArray();
        this.numElements = count;
    }

    @Override
    public void run(SourceContext<T> ctx) throws Exception {
        ByteArrayInputStream bais  = new ByteArrayInputStream(elementsSerialized);
        final DataInputView  input = new DataInputViewStreamWrapper(bais);

        // if we are restored from a checkpoint and need to skip elements, skip them now.
        int toSkip = numElementsToSkip;
        if (toSkip > 0) {
            try {
                while (toSkip > 0) {
                    serializer.deserialize(input);
                    toSkip--;
                }
            } catch (Exception e) {
                throw new IOException("Failed to deserialize an element from the source. " +
                        "If you are using user-defined serialization (Value and Writable types), check the " +
                        "serialization functions.\nSerializer is " + serializer, e);
            }

            this.numElementsEmitted = this.numElementsToSkip;
        }

        final Object lock = ctx.getCheckpointLock();

        while (isRunning && numElementsEmitted < numElements) {
            T next;
            try {
                next = serializer.deserialize(input);
            } catch (Exception e) {
                throw new IOException("Failed to deserialize an element from the source. " +
                        "If you are using user-defined serialization (Value and Writable types), check the " +
                        "serialization functions.\nSerializer is " + serializer, e);
            }

            synchronized (lock) {
                ctx.collect(next);
                numElementsEmitted++;
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
