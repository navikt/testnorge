package no.nav.testnav.libs.reactivecore.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.SocketException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note: {@code io.netty.channel.unix.Errors.NativeIoException} (thrown by reactor-netty's
 * native epoll/kqueue transport on a connection reset, e.g. "recvAddress(..) failed with
 * error(-104)") is deliberately not instantiated here. Its static initializer calls into a
 * JNI native library that is only loaded when the native transport is actually active, which
 * makes it unsafe to construct in a plain unit test across environments. The fix that added
 * this exception type to {@link WebClientError}'s retry predicate was instead verified by
 * inspecting the compiled class hierarchy ({@code NativeIoException extends java.io.IOException},
 * not {@link SocketException}), which is why it was not already covered by the existing
 * {@code SocketException} branch below.
 */
class WebClientErrorTest {

    private static final URI URI_NOWHERE = URI.create("https://nowhere.net");

    @Test
    void shouldRetryOn5xxResponse() {
        assertThatRetries(WebClientResponseException.create(500, "Internal Server Error", null, null, null));
    }

    @Test
    void shouldNotRetryOn4xxResponse() {
        assertThatDoesNotRetry(WebClientResponseException.create(404, "Not Found", null, null, null));
    }

    @Test
    void shouldRetryOnSocketException() {
        assertThatRetries(requestException(new SocketException("Connection reset")));
    }

    @Test
    void shouldRetryAfterOneFiveAndTenSeconds() {
        var attempts = new AtomicInteger();
        var throwable = WebClientResponseException.create(500, "Internal Server Error", null, null, null);

        StepVerifier.withVirtualTime(() -> Flux.defer(() ->
                                attempts.incrementAndGet() < 4 ? Flux.error(throwable) : Flux.just("ok"))
                        .retryWhen(WebClientError.is5xxException()))
                .expectSubscription()
                .then(() -> assertThat(attempts.get()).isEqualTo(1))
                .expectNoEvent(Duration.ofMillis(999))
                .thenAwait(Duration.ofMillis(1))
                .then(() -> assertThat(attempts.get()).isEqualTo(2))
                .expectNoEvent(Duration.ofMillis(4_999))
                .thenAwait(Duration.ofMillis(1))
                .then(() -> assertThat(attempts.get()).isEqualTo(3))
                .expectNoEvent(Duration.ofMillis(9_999))
                .thenAwait(Duration.ofMillis(1))
                .expectNext("ok")
                .verifyComplete();

        assertThat(attempts.get()).isEqualTo(4);
    }

    @Test
    void shouldRetryAtMostThreeTimes() {
        var attempts = new AtomicInteger();
        var throwable = WebClientResponseException.create(500, "Internal Server Error", null, null, null);

        StepVerifier.withVirtualTime(() -> Flux.defer(() -> {
                            attempts.incrementAndGet();
                            return Flux.error(throwable);
                        })
                        .retryWhen(WebClientError.is5xxException()))
                .thenAwait(Duration.ofSeconds(16))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(throwable))
                .verify();

        assertThat(attempts.get()).isEqualTo(4);
    }

    @Test
    void shouldPropagateOriginalSocketExceptionAfterThreeRetries() {
        var attempts = new AtomicInteger();
        var throwable = requestException(new SocketException("Connection reset"));

        StepVerifier.withVirtualTime(() -> Flux.defer(() -> {
                            attempts.incrementAndGet();
                            return Flux.error(throwable);
                        })
                        .retryWhen(WebClientError.is5xxException()))
                .thenAwait(Duration.ofSeconds(16))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(throwable))
                .verify();

        assertThat(attempts.get()).isEqualTo(4);
    }

    @Test
    void shouldStopRetryingWhenAFollowingFailureIsNotRetryable() {
        var attempts = new AtomicInteger();
        var serverError = WebClientResponseException.create(503, "Service Unavailable", null, null, null);
        var clientError = WebClientResponseException.create(400, "Bad Request", null, null, null);

        StepVerifier.withVirtualTime(() -> Flux.defer(() -> {
                            var attempt = attempts.incrementAndGet();
                            return Flux.error(attempt == 1 ? serverError : clientError);
                        })
                        .retryWhen(WebClientError.is5xxException()))
                .thenAwait(Duration.ofSeconds(1))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(clientError))
                .verify();

        assertThat(attempts.get()).isEqualTo(2);
    }

    @Test
    void shouldRetryDifferent5xxResponsesWithinTheSameSequence() {
        var attempts = new AtomicInteger();

        StepVerifier.withVirtualTime(() -> Flux.defer(() -> {
                            var attempt = attempts.incrementAndGet();
                            return switch (attempt) {
                                case 1 -> Flux.error(WebClientResponseException.create(
                                        500, "Internal Server Error", null, null, null));
                                case 2 -> Flux.error(WebClientResponseException.create(
                                        502, "Bad Gateway", null, null, null));
                                case 3 -> Flux.error(WebClientResponseException.create(
                                        503, "Service Unavailable", null, null, null));
                                default -> Flux.just("ok");
                            };
                        })
                        .retryWhen(WebClientError.is5xxException()))
                .thenAwait(Duration.ofSeconds(16))
                .expectNext("ok")
                .verifyComplete();

        assertThat(attempts.get()).isEqualTo(4);
    }

    @Test
    void shouldNotRetryOnUnknownRequestExceptionCause() {
        assertThatDoesNotRetry(requestException(new RuntimeException("noe annet gikk galt")));
    }

    private static WebClientRequestException requestException(Throwable cause) {
        return new WebClientRequestException(cause, HttpMethod.GET, URI_NOWHERE, HttpHeaders.EMPTY);
    }

    private static void assertThatRetries(Throwable throwable) {
        var attempts = new AtomicInteger();

        StepVerifier.withVirtualTime(() -> Flux.defer(() -> attempts.incrementAndGet() < 2 ? Flux.error(throwable) : Flux.just("ok"))
                        .retryWhen(WebClientError.is5xxException()))
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("ok")
                .verifyComplete();

        assertThat(attempts.get()).isEqualTo(2);
    }

    private static void assertThatDoesNotRetry(Throwable throwable) {
        var attempts = new AtomicInteger();

        StepVerifier.create(Flux.defer(() -> {
                            attempts.incrementAndGet();
                            return Flux.error(throwable);
                        })
                        .retryWhen(WebClientError.is5xxException()))
                .expectError(throwable.getClass())
                .verify();

        assertThat(attempts.get()).isEqualTo(1);
    }
}
