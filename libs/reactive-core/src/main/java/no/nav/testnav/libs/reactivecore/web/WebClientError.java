package no.nav.testnav.libs.reactivecore.web;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.micrometer.common.util.StringUtils.isNotBlank;

/**
 * Convenience class for handling error situations when using {@link WebClient} instances.
 */
@UtilityClass
@Slf4j
public class WebClientError {

    private static final String DUAL_PARMS = "{} {}";

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
     * Convenience method for logging.
     *
     * @param logger The logger to log to, for improved readability. Cannot be resolved from stack trace on call.
     */
    public static Consumer<Throwable> logTo(Logger logger) {
        return throwable -> {
            if ((throwable instanceof WebClientResponseException webClientResponseException)) {
                if (webClientResponseException.getStatusCode().is4xxClientError()) {
                    logger.warn(
                            DUAL_PARMS,
                            webClientResponseException.getMessage(),
                            webClientResponseException.getResponseBodyAsString()
                    );
                } else {
                    logger.error(
                            DUAL_PARMS,
                            webClientResponseException.getMessage(),
                            webClientResponseException.getResponseBodyAsString(),
                            throwable
                    );
                }
            } else {
                logger.error(throwable.getMessage(), throwable);
            }
        };
    }

    /**
     * Describes a {@link Throwable} as a {@link Description} object, containing a message and a status.
     * For convenience.
     *
     * @param throwable The {@link Throwable} to describe.
     * @return A {@link Description} object.
     */
    public static Description describe(Throwable throwable) {
        return new Description(throwable);
    }

    /**
     * Describes a {@link Throwable} as a {@link Description} object, containing a message and a status.
     */
    @Getter
    public static class Description {

        private static final String MSG_TIMEOUT = "Mottaker svarer ikke, eller har for lang svartid.";
        private static final String MSG_UNSTABLE = "Forbindelsen er ustabil og mottaker kunne ikke nås.";

        private final String message;
        private final HttpStatus status;

        private Description(Throwable throwable) {
            message = resolveMessage(throwable);
            status = resolveStatus(throwable);
        }

        private static String resolveMessage(Throwable throwable) {
            switch (throwable) {
                case WebClientResponseException e -> {
                    return isNotBlank(e.getResponseBodyAsString(StandardCharsets.UTF_8)) ?
                            e.getResponseBodyAsString(StandardCharsets.UTF_8) :
                            e.getStatusCode().value() + " " + e.getStatusText();
                }
                case WebClientRequestException e -> {
                    switch (e.getCause()) {
                        case ConnectTimeoutException ignored -> {
                            return MSG_TIMEOUT;
                        }
                        case ReadTimeoutException ignored -> {
                            return MSG_TIMEOUT;
                        }
                        case WriteTimeoutException ignored -> {
                            return MSG_TIMEOUT;
                        }
                        case SocketException ignored -> {
                            return MSG_UNSTABLE;
                        }
                        default -> {
                            return e.getCause().toString();
                        }
                    }
                }
                case TimeoutException ignored -> {
                    return MSG_TIMEOUT;
                }
                default -> {
                    return throwable.getMessage();
                }
            }
        }

        private static HttpStatus resolveStatus(Throwable throwable) {
            switch (throwable) {
                case WebClientResponseException e -> {
                    return HttpStatus.valueOf(e.getStatusCode().value());
                }
                case TimeoutException ignored -> {
                    return HttpStatus.REQUEST_TIMEOUT;
                }
                default -> {
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }
        }
    }
}