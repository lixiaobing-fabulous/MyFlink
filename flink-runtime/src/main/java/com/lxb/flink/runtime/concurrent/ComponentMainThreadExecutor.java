package com.lxb.flink.runtime.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ComponentMainThreadExecutor extends ScheduledExecutor {
    void assertRunningInMainThread();

    final class DummyComponentMainThreadExecutor implements ComponentMainThreadExecutor {

        /**
         * Customized message for the exception that is thrown on method invocation.
         */
        private final String exceptionMessageOnInvocation;

        public DummyComponentMainThreadExecutor(String exceptionMessageOnInvocation) {
            this.exceptionMessageOnInvocation = exceptionMessageOnInvocation;
        }

        @Override
        public void assertRunningInMainThread() {
            throw createException();
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            throw createException();
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            throw createException();
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            throw createException();
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            throw createException();
        }

        @Override
        public void execute(Runnable command) {
            throw createException();
        }

        private UnsupportedOperationException createException() {
            return new UnsupportedOperationException(exceptionMessageOnInvocation);
        }
    }
}
