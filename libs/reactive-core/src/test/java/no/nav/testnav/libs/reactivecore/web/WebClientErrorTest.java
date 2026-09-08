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
