package com.lxb.flink.api.common.restartstrategy;

import com.lxb.flink.api.common.time.Time;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RestartStrategies {
    public static RestartStrategyConfiguration noRestart() {
        return new NoRestartStrategyConfiguration();
    }

    public static RestartStrategyConfiguration fallBackRestart() {
        return new FallbackRestartStrategyConfiguration();
    }

    public static RestartStrategyConfiguration fixedDelayRestart(int restartAttempts, long delayBetweenAttempts) {
        return fixedDelayRestart(restartAttempts, Time.of(delayBetweenAttempts, TimeUnit.MILLISECONDS));
    }

    public static RestartStrategyConfiguration fixedDelayRestart(int restartAttempts, Time delayInterval) {
        return new FixedDelayRestartStrategyConfiguration(restartAttempts, delayInterval);
    }

    public static FailureRateRestartStrategyConfiguration failureRateRestart(
            int failureRate, Time failureInterval, Time delayInterval) {
        return new FailureRateRestartStrategyConfiguration(failureRate, failureInterval, delayInterval);
    }

    public abstract static class RestartStrategyConfiguration implements Serializable {

        private RestartStrategyConfiguration() {
        }

        public abstract String getDescription();

        @Override
        public String toString() {
            return getDescription();
        }
    }

    public static final class NoRestartStrategyConfiguration extends RestartStrategyConfiguration {

        @Override
        public String getDescription() {
            return "Restart deactivated.";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o instanceof NoRestartStrategyConfiguration;
        }

        @Override
        public int hashCode() {
            return Objects.hash();
        }
    }

    public static final class FixedDelayRestartStrategyConfiguration extends RestartStrategyConfiguration {
        private static final long serialVersionUID = 4149870149673363190L;

        private final int  restartAttempts;
        private final Time delayBetweenAttemptsInterval;

        FixedDelayRestartStrategyConfiguration(int restartAttempts, Time delayBetweenAttemptsInterval) {
            this.restartAttempts = restartAttempts;
            this.delayBetweenAttemptsInterval = delayBetweenAttemptsInterval;
        }

        public int getRestartAttempts() {
            return restartAttempts;
        }

        public Time getDelayBetweenAttemptsInterval() {
            return delayBetweenAttemptsInterval;
        }

        @Override
        public int hashCode() {
            int result = restartAttempts;
            result = 31 * result + (delayBetweenAttemptsInterval != null ? delayBetweenAttemptsInterval.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FixedDelayRestartStrategyConfiguration) {
                FixedDelayRestartStrategyConfiguration other = (FixedDelayRestartStrategyConfiguration) obj;

                return restartAttempts == other.restartAttempts &&
                        delayBetweenAttemptsInterval.equals(other.delayBetweenAttemptsInterval);
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return String.format(
                    "Restart with fixed delay (%s). #%d restart attempts.",
                    delayBetweenAttemptsInterval,
                    restartAttempts);
        }
    }

    public static final class FailureRateRestartStrategyConfiguration extends RestartStrategyConfiguration {
        private static final long serialVersionUID = 1195028697539661739L;
        private final        int  maxFailureRate;

        private final Time failureInterval;
        private final Time delayBetweenAttemptsInterval;

        public FailureRateRestartStrategyConfiguration(
                int maxFailureRate,
                Time failureInterval,
                Time delayBetweenAttemptsInterval) {
            this.maxFailureRate = maxFailureRate;
            this.failureInterval = failureInterval;
            this.delayBetweenAttemptsInterval = delayBetweenAttemptsInterval;
        }

        public int getMaxFailureRate() {
            return maxFailureRate;
        }

        public Time getFailureInterval() {
            return failureInterval;
        }

        public Time getDelayBetweenAttemptsInterval() {
            return delayBetweenAttemptsInterval;
        }

        @Override
        public String getDescription() {
            return String.format(
                    "Failure rate restart with maximum of %d failures within interval %s and fixed delay %s.",
                    maxFailureRate,
                    failureInterval.toString(),
                    delayBetweenAttemptsInterval.toString());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FailureRateRestartStrategyConfiguration that = (FailureRateRestartStrategyConfiguration) o;
            return maxFailureRate == that.maxFailureRate &&
                    Objects.equals(failureInterval, that.failureInterval) &&
                    Objects.equals(delayBetweenAttemptsInterval, that.delayBetweenAttemptsInterval);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maxFailureRate, failureInterval, delayBetweenAttemptsInterval);
        }
    }

    public static final class FallbackRestartStrategyConfiguration extends RestartStrategyConfiguration {
        private static final long serialVersionUID = -4441787204284085544L;

        @Override
        public String getDescription() {
            return "Cluster level default restart strategy";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o instanceof FallbackRestartStrategyConfiguration;
        }

        @Override
        public int hashCode() {
            return Objects.hash();
        }
    }

}
