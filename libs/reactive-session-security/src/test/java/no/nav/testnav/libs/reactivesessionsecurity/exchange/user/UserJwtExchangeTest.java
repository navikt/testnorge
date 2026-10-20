package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserJwtExchangeTest {

    private static final String USER_ID = "fake-user-id";
    private static final String ACCESS_TOKEN = "fake-access-token";

    @Mock
    private TokenXExchange tokenExchange;

    @Test
    void shouldRequestFreshAzureUserJwt() {
        var requestCount = new AtomicInteger();
        var personalUserJwt = createToken(Duration.ofHours(1), null);
        var teamUserJwt = createToken(Duration.ofHours(1), "team-bruker-id-42");
        var userJwtExchange = userJwtExchange(request -> {
            var token = requestCount.getAndIncrement() == 0 ? personalUserJwt : teamUserJwt;
            return response(token);
        });

        StepVerifier.create(userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN)
                        .then(userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN)))
                .assertNext(token -> assertThat(JWT.decode(token)
                        .getClaim("representingTeam")
                        .asString()).isEqualTo("team-bruker-id-42"))
                .verifyComplete();

        assertThat(requestCount).hasValue(2);
    }

    @Test
    void shouldShareConcurrentAzureUserJwtRequest() {
        var requestCount = new AtomicInteger();
        var userJwt = createToken(Duration.ofHours(1), null);
        var userJwtExchange = userJwtExchange(request -> Mono.defer(() -> {
            requestCount.incrementAndGet();
            return Mono.delay(Duration.ofMillis(25)).then(response(userJwt));
        }));

        StepVerifier.create(Flux.merge(
                                userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN),
                                userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN))
                        .collectList())
                .assertNext(tokens -> assertThat(tokens).containsExactly(userJwt, userJwt))
                .verifyComplete();

        assertThat(requestCount).hasValue(1);
    }

    @Test
    void shouldNotCacheFailedUserJwtRequest() {
        var requestCount = new AtomicInteger();
        var userJwt = createToken(Duration.ofHours(1), null);
        var userJwtExchange = userJwtExchange(request -> Mono.defer(() ->
                requestCount.getAndIncrement() == 0
                        ? Mono.error(new IllegalStateException("request failed"))
                        : response(userJwt)));

        StepVerifier.create(userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN))
                .expectError(IllegalStateException.class)
                .verify();
        StepVerifier.create(userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN))
                .expectNext(userJwt)
                .verifyComplete();

        assertThat(requestCount).hasValue(2);
    }

    @Test
    void shouldRejectUserJwtWithoutExpiration() {
        var userJwt = JWT.create().sign(Algorithm.HMAC256("secret"));
        var userJwtExchange = userJwtExchange(request -> response(userJwt));

        StepVerifier.create(userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN))
                .expectErrorMatches(error -> error.getMessage().equals("User-Jwt mangler utløpstid."))
                .verify();
    }

    @Test
    void shouldRejectUserJwtExpiringWithinCacheMargin() {
        var userJwt = createToken(Duration.ofMinutes(4), null);
        var userJwtExchange = userJwtExchange(request -> response(userJwt));

        StepVerifier.create(userJwtExchange.generateJwtWithAccessToken(USER_ID, ACCESS_TOKEN))
                .expectErrorMatches(error ->
                        error.getMessage().equals("User-Jwt er utløpt eller utløper for snart."))
                .verify();
    }

    private UserJwtExchange userJwtExchange(ExchangeFunction exchangeFunction) {
        var properties = new TestnavBrukerServiceProperties();
        properties.setUrl("http://testnav-bruker-service");
        var webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .exchangeFunction(exchangeFunction)
                .build();
        return new UserJwtExchange(properties, tokenExchange, webClient);
    }

    private static Mono<ClientResponse> response(String token) {
        return Mono.just(ClientResponse
                .create(HttpStatus.OK)
                .body(token)
                .build());
    }

    private static String createToken(Duration lifetime, String representingTeam) {
        var builder = JWT.create()
                .withExpiresAt(Instant.now().plus(lifetime));
        if (representingTeam != null) {
            builder.withClaim("representingTeam", representingTeam);
        }
        return builder.sign(Algorithm.HMAC256("secret"));
    }
}
