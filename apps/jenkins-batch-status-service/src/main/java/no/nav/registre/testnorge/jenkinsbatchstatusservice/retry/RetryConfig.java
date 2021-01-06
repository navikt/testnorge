package no.nav.registre.testnorge.jenkinsbatchstatusservice.retry;

public class RetryConfig {
    private final int retryAttempts;
    private final long sleepMilliseconds;

    private RetryConfig(int retryAttempts, long sleepMilliseconds) {
        this.retryAttempts = retryAttempts;
        this.sleepMilliseconds = sleepMilliseconds;
    }

    public static class Builder {
        private int retryAttempts;
        private long sleepMilliseconds;

        public Builder setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
            return this;
        }

        public Builder setSleepSeconds(long seconds) {
            this.sleepMilliseconds = seconds * 1000;
            return this;
        }

        public RetryConfig build() {
            return new RetryConfig(retryAttempts, sleepMilliseconds);
        }
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public long getSleepMilliseconds() {
        return sleepMilliseconds;
    }
}
