package com.lxb.flink.runtime.io;

import java.util.concurrent.CompletableFuture;

import com.lxb.flink.annotation.Internal;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-29
 */
@Internal
public interface AvailabilityProvider {
    /**
     * Constant that allows to avoid volatile checks {@link CompletableFuture#isDone()}. Check
     * {@link #isAvailable()} and {@link #isApproximatelyAvailable()} for more explanation.
     */
    CompletableFuture<?> AVAILABLE = CompletableFuture.completedFuture(null);

    /**
     * @return a future that is completed if the respective provider is available.
     */
    CompletableFuture<?> getAvailableFuture();

    /**
     * In order to best-effort avoid volatile access in {@link CompletableFuture#isDone()}, we check the condition
     * of <code>future == AVAILABLE</code> firstly for getting probable performance benefits while hot looping.
     *
     * <p>It is always safe to use this method in performance nonsensitive scenarios to get the precise state.
     *
     * @return true if this instance is available for further processing.
     */
    default boolean isAvailable() {
        CompletableFuture<?> future = getAvailableFuture();
        return future == AVAILABLE || future.isDone();
    }

    /**
     * Checks whether this instance is available only via constant {@link #AVAILABLE} to avoid
     * performance concern caused by volatile access in {@link CompletableFuture#isDone()}. So it is
     * mainly used in the performance sensitive scenarios which do not always need the precise state.
     *
     * <p>This method is still safe to get the precise state if {@link #getAvailableFuture()}
     * was touched via (.get(), .wait(), .isDone(), ...) before, which also has a "happen-before"
     * relationship with this call.
     *
     * @return true if this instance is available for further processing.
     */
    default boolean isApproximatelyAvailable() {
        return getAvailableFuture() == AVAILABLE;
    }

    /**
     * A availability implementation for providing the helpful functions of resetting the
     * available/unavailable states.
     */
    final class AvailabilityHelper implements AvailabilityProvider {

        private CompletableFuture<?> availableFuture = new CompletableFuture<>();

        /**
         * Judges to reset the current available state as unavailable.
         */
        public void resetUnavailable() {
            if (isAvailable()) {
                availableFuture = new CompletableFuture<>();
            }
        }

        /**
         * Resets the constant completed {@link #AVAILABLE} as the current state.
         */
        public void resetAvailable() {
            availableFuture = AVAILABLE;
        }

        /**
         *  Returns the previously not completed future and resets the constant completed
         *  {@link #AVAILABLE} as the current state.
         */
        public CompletableFuture<?> getUnavailableToResetAvailable() {
            CompletableFuture<?> toNotify = availableFuture;
            availableFuture = AVAILABLE;
            return toNotify;
        }

        /**
         *  Creates a new uncompleted future as the current state and returns the
         *  previous uncompleted one.
         */
        public CompletableFuture<?> getUnavailableToResetUnavailable() {
            CompletableFuture<?> toNotify = availableFuture;
            availableFuture = new CompletableFuture<>();
            return toNotify;
        }

        /**
         * @return a future that is completed if the respective provider is available.
         */
        @Override
        public CompletableFuture<?> getAvailableFuture() {
            return availableFuture;
        }
    }
}
