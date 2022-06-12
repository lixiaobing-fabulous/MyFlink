package com.lxb.flink.utl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface AutoCloseableAsync extends AutoCloseable {
    CompletableFuture<Void> closeAsync();

    default void close() throws Exception {
        try {
            closeAsync().get();
        } catch (ExecutionException e) {
            throw new FlinkException("Could not close resource.", ExceptionUtils.stripExecutionException(e));
        }
    }

}
