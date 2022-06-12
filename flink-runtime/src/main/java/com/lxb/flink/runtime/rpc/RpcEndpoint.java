package com.lxb.flink.runtime.rpc;

import com.lxb.flink.api.common.time.Time;
import com.lxb.flink.runtime.concurrent.ComponentMainThreadExecutor;
import com.lxb.flink.runtime.concurrent.ScheduledFutureAdapter;
import com.lxb.flink.utl.AutoCloseableAsync;
import com.lxb.flink.utl.Preconditions;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

public abstract class RpcEndpoint implements RpcGateway, AutoCloseableAsync {
    private final   RpcService              rpcService;
    private final   String                  endpointId;
    protected final RpcServer               rpcServer;
    final           AtomicReference<Thread> currentMainThread = new AtomicReference<>(null);
    private final   MainThreadExecutor      mainThreadExecutor;
    private         boolean                 isRunning;

    protected RpcEndpoint(final RpcService rpcService, final String endpointId) {
        this.rpcService = checkNotNull(rpcService, "rpcService");
        this.endpointId = checkNotNull(endpointId, "endpointId");

        this.rpcServer = rpcService.startServer(this);

        this.mainThreadExecutor = new MainThreadExecutor(rpcServer, this::validateRunsInMainThread);
    }

    public void validateRunsInMainThread() {
        assert MainThreadValidatorUtil.isRunningInExpectedThread(currentMainThread.get());
    }

    protected RpcEndpoint(final RpcService rpcService) {
        this(rpcService, UUID.randomUUID().toString());
    }

    public String getEndpointId() {
        return endpointId;
    }

    protected boolean isRunning() {
        validateRunsInMainThread();
        return isRunning;
    }

    public final void start() {
        rpcServer.start();
    }

    public final void internalCallOnStart() throws Exception {
        validateRunsInMainThread();
        isRunning = true;
        onStart();
    }

    protected void onStart() throws Exception {
    }

    protected final void stop() {
        rpcServer.stop();
    }

    public final CompletableFuture<Void> internalCallOnStop() {
        validateRunsInMainThread();
        CompletableFuture<Void> stopFuture = onStop();
        isRunning = false;
        return stopFuture;
    }

    protected CompletableFuture<Void> onStop() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public final CompletableFuture<Void> closeAsync() {
        rpcService.stopServer(rpcServer);
        return getTerminationFuture();
    }

    public CompletableFuture<Void> getTerminationFuture() {
        return rpcServer.getTerminationFuture();
    }

    public <C extends RpcGateway> C getSelfGateway(Class<C> selfGatewayType) {
        if (selfGatewayType.isInstance(rpcServer)) {
            @SuppressWarnings("unchecked")
            C selfGateway = ((C) rpcServer);

            return selfGateway;
        } else {
            throw new RuntimeException("RpcEndpoint does not implement the RpcGateway interface of type " + selfGatewayType + '.');
        }
    }

    @Override
    public String getAddress() {
        return rpcServer.getAddress();
    }

    @Override
    public String getHostname() {
        return rpcServer.getHostname();
    }

    protected MainThreadExecutor getMainThreadExecutor() {
        return mainThreadExecutor;
    }

    public RpcService getRpcService() {
        return rpcService;
    }

    protected void runAsync(Runnable runnable) {
        rpcServer.runAsync(runnable);
    }

    protected void scheduleRunAsync(Runnable runnable, Time delay) {
        scheduleRunAsync(runnable, delay.getSize(), delay.getUnit());
    }

    protected void scheduleRunAsync(Runnable runnable, long delay, TimeUnit unit) {
        rpcServer.scheduleRunAsync(runnable, unit.toMillis(delay));
    }

    protected <V> CompletableFuture<V> callAsync(Callable<V> callable, Time timeout) {
        return rpcServer.callAsync(callable, timeout);
    }

    protected static class MainThreadExecutor implements ComponentMainThreadExecutor {

        private final MainThreadExecutable gateway;
        private final Runnable             mainThreadCheck;

        MainThreadExecutor(MainThreadExecutable gateway, Runnable mainThreadCheck) {
            this.gateway = checkNotNull(gateway);
            this.mainThreadCheck = checkNotNull(mainThreadCheck);
        }

        public void runAsync(Runnable runnable) {
            gateway.runAsync(runnable);
        }

        public void scheduleRunAsync(Runnable runnable, long delayMillis) {
            gateway.scheduleRunAsync(runnable, delayMillis);
        }

        public void execute(Runnable command) {
            runAsync(command);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            final long       delayMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
            FutureTask<Void> ft          = new FutureTask<>(command, null);
            scheduleRunAsync(ft, delayMillis);
            return new ScheduledFutureAdapter<>(ft, delayMillis, TimeUnit.MILLISECONDS);
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException("Not implemented because the method is currently not required.");
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            throw new UnsupportedOperationException("Not implemented because the method is currently not required.");
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException("Not implemented because the method is currently not required.");
        }

        @Override
        public void assertRunningInMainThread() {
            mainThreadCheck.run();
        }
    }
}
