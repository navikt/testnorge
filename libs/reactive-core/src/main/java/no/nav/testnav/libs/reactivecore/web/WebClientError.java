package no.nav.testnav.libs.reactivecore.web;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.net.SocketException;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * <p>Convenience class for configuring {@link org.springframework.web.reactive.function.client.WebClient} instances.</p>
 * <p>Also includes a non-static (for stack trace) logging handler, for uniform logging muted during retries.</p>
 */
@UtilityClass
@Slf4j
public class WebClientError {

    private static final Predicate<Throwable> IS_5XX = throwable -> throwable instanceof WebClientResponseException webClientResponseException &&
            webClientResponseException.getStatusCode().is5xxServerError() ||
            throwable instanceof WebClientRequestException webClientRequestException &&
                    webClientRequestException.getCause() instanceof SocketException;

    /**
     * Returns a {@link Retry} that will retry on the given exception type, for a given number of times, after a given set of seconds.
     *
     * @param on           Exception type.
     * @param times        Times to retry.
     * @param afterSeconds Seconds to wait between retries.
     * @return Retry configuration (actually a {@link RetryBackoffSpec}).
     */
    public static Retry is(Predicate<Throwable> on, long times, long afterSeconds) {
        return Retry
                .backoff(times, Duration.ofSeconds(afterSeconds))
                .filter(on);
    }

    /**
     * Returns a {@link Retry} that will retry on the given exception type, for 3 times, with a 5 second delay between retries.
     *
     * @param on Exception type.
     * @return Retry configuration (actually a {@link RetryBackoffSpec}).
     */
    public static Retry is(Predicate<Throwable> on) {
        return is(on, 3, 5);
    }

    /**
     * Returns a {@link Retry} that will retry on any exception, for 3 times, with a 5 second delay between retries.
     *
     * @return Retry configuration (actually a {@link RetryBackoffSpec}).
     */
    public static Retry any() {
        return is(throwable -> true);
    }

    /**
     * Convenience method for retrying on 5xx exceptions. Similar to {@link #any()}.
     *
     * @return Retry configuration (actually a {@link RetryBackoffSpec}).
     */
    public static Retry is5xxException() {
        return is(IS_5XX);
    }

    /**
     * Convenience method for retrying on 5xx exceptions, then throwing a given exception. Similar to {@link #is5xxException()}.
     *
     * @param throwable A {@link Throwable} to throw after retries are exhausted.
     * @return Retry configuration (actually a {@link RetryBackoffSpec}).
     */
    public static Retry is5xxExceptionThen(Throwable throwable) {
        return ((RetryBackoffSpec) is5xxException()).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> throwable);
    }

    /**
     * Convenience method for logging, using {@code .doOnError(throwable -> WebClientError.log(throwable, log))}.
     * @param throwable The error to log.
     * @param logger The logger to log to, for improved readability. Cannot be resolved from stack trace on call.
     */
    public static void log(Throwable throwable, Logger logger) {
        if ((throwable instanceof WebClientResponseException webClientResponseException)) {
            logger.error(
                    "{}, {}",
                    throwable.getMessage(),
                    webClientResponseException.getResponseBodyAsString(),
                    throwable
            );
        } else {
            logger.error(throwable.getMessage(), throwable);
        }
    }

    /*public static <T> Mono<T> logX(Throwable throwable, Logger logger) {
        if ((throwable instanceof WebClientResponseException webClientResponseException)) {
            logger.error(
                    "{}, {}",
                    throwable.getMessage(),
                    webClientResponseException.getResponseBodyAsString(),
                    throwable
            );
        } else {
            logger.error(throwable.getMessage(), throwable);
        }
        return Mono.error(throwable);
    }*/

}
