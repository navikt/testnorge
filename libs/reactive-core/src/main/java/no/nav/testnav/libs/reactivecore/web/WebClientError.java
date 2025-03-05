package no.nav.testnav.libs.reactivecore.web;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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

    /**
     * <p>Returns a {@link Retry} that will retry on the given exception type, for a given number of times, after a given set of seconds.</p>
     * <p>Note that the logging from {@link Handler#handle(ClientResponse)} is turned OFF until the last retry attempt, to avoid repetitive logging.</p>
     *
     * @param on           Exception type.
     * @param times        Times to retry.
     * @param afterSeconds Seconds to wait between retries.
     * @return Retry configuration.
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
     * @return Retry configuration.
     */
    public static Retry is(Predicate<Throwable> on) {
        return is(on, 3, 5);
    }

    /**
     * Returns a {@link Retry} that will retry on any exception, for 3 times, with a 5 second delay between retries.
     *
     * @return Retry configuration.
     */
    public static Retry any() {
        return is(throwable -> true);
    }

    /**
     * <p>A custom logging handler, using its stack trace at construction time to log any error handled within a {@link org.springframework.web.reactive.function.client.WebClient} spec.</p>
     * <p>That means, use the constructor outside the {@link org.springframework.web.reactive.function.client.WebClient} spec itself.</p>
     */
    public static class Handler {

        private final StackTraceElement[] constructorStackTrace;

        /**
         * Use this constructor outside {@link org.springframework.web.reactive.function.client.WebClient} spec to get a relevant stack trace to this point in code on any errors.
         */
        public Handler() {
            var currentStackTrace = Thread.currentThread().getStackTrace();
            constructorStackTrace = Arrays.copyOfRange(currentStackTrace, 2, currentStackTrace.length); // Removes the first two elements, which are Thread.getStackTrace and this constructor.
        }

        /**
         * Handle an error. Example use is {@code .onStatus(HttpStatusCode::isError, webClientErrorHandler::handle)}. Rewrites the stack trace to the point where this {@code Handler} was instantiated.
         * @param response The response that caused the error.
         * @return A {@link Mono} that will throw a {@link ResponseStatusException} with the status code and reason, with a rewritten stack trace.
         */
        public Mono<Throwable> handle(ClientResponse response) {
            var exception = new ResponseStatusException(response.statusCode());
            exception.setStackTrace(constructorStackTrace); // Replace the stack trace (which is Reactive only) with the stack trace into our code where this Handler was instantiated.
            return Mono.error(exception);
        }

        /**
         * Log an error. Example use is {@code .doOnError(webClientErrorHandler::log)}.
         * @param throwable The error to log.
         * @return A {@link Mono} that will throw the same error.
         */
        public Mono<Throwable> log(Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            return Mono.error(throwable);
        }

    }

}
