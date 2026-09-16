package no.nav.testnav.apps.templatesearchservice.consumers;

import no.nav.testnav.apps.templatesearchservice.config.Consumers;
import no.nav.testnav.apps.templatesearchservice.exception.DollyBackendUnavailableException;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DollyBackendConsumerTest {

    @Mock
    private Consumers consumers;

    @Mock
    private TokenExchange tokenExchange;

    @ParameterizedTest
    @ValueSource(strings = {"dolly-backend", "dolly-backend-dev"})
    void shouldUseConfiguredBackendAndReturnUnprefixedTeamId(String backend) {
        var request = new AtomicReference<ClientRequest>();
        var properties = properties(backend);
        when(tokenExchange.exchange(properties)).thenReturn(Mono.just(new AccessToken("obo-token")));
        var consumer = consumer(properties, request, HttpStatus.OK, """
                {
                  "brukerId": "azure-user",
                  "representererTeam": {
                    "brukerId": "team-bruker-id-42",
                    "navn": "Testteam"
                  }
                }
                """);

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectNext("team-bruker-id-42")
                .verifyComplete();

        assertThat(request.get().url().getHost()).isEqualTo(backend);
        assertThat(request.get().url().getPath()).isEqualTo("/api/v1/bruker/current");
        assertThat(request.get().headers().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer obo-token");
        verify(tokenExchange).exchange(properties);
    }

    @Test
    void shouldReturnEmptyOnlyWhenDollyReportsNoActiveTeam() {
        var properties = properties("dolly-backend");
        when(tokenExchange.exchange(properties)).thenReturn(Mono.just(new AccessToken("obo-token")));
        var consumer = consumer(properties, new AtomicReference<>(), HttpStatus.OK, """
                {"representererTeam":null}
                """);

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void shouldRejectTeamWithoutUserId(String teamUserId) {
        var properties = properties("dolly-backend");
        when(tokenExchange.exchange(properties)).thenReturn(Mono.just(new AccessToken("obo-token")));
        var value = "null".equals(teamUserId) ? "null" : "\"" + teamUserId + "\"";
        var consumer = consumer(properties, new AtomicReference<>(), HttpStatus.OK,
                "{\"representererTeam\":{\"brukerId\":" + value + "}}");

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldFailWhenDollyReturnsNoBody() {
        var properties = properties("dolly-backend");
        when(tokenExchange.exchange(properties)).thenReturn(Mono.just(new AccessToken("obo-token")));
        var consumer = consumer(properties, new AtomicReference<>(), HttpStatus.OK, "");

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectError(DollyBackendUnavailableException.class)
                .verify();
    }

    @Test
    void shouldPropagateDollyFailureWithoutPersonalFallback() {
        var properties = properties("dolly-backend");
        when(tokenExchange.exchange(properties)).thenReturn(Mono.just(new AccessToken("obo-token")));
        var consumer = consumer(properties, new AtomicReference<>(), HttpStatus.SERVICE_UNAVAILABLE, "");

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectError(DollyBackendUnavailableException.class)
                .verify();
    }

    @Test
    void shouldPropagateTokenExchangeFailureWithoutCallingDolly() {
        var properties = properties("dolly-backend");
        when(tokenExchange.exchange(properties)).thenReturn(Mono.error(new IllegalStateException("Token exchange")));
        var request = new AtomicReference<ClientRequest>();
        var consumer = consumer(properties, request, HttpStatus.OK, "{}");

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectError(DollyBackendUnavailableException.class)
                .verify();

        assertThat(request.get()).isNull();
    }

    @Test
    void shouldRejectMissingExchangedTokenWithoutCallingDolly() {
        var properties = properties("dolly-backend");
        when(tokenExchange.exchange(properties)).thenReturn(Mono.empty());
        var request = new AtomicReference<ClientRequest>();
        var consumer = consumer(properties, request, HttpStatus.OK, "{}");

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectError(DollyBackendUnavailableException.class)
                .verify();

        assertThat(request.get()).isNull();
    }

    private DollyBackendConsumer consumer(
            ServerProperties properties,
            AtomicReference<ClientRequest> request,
            HttpStatus status,
            String body
    ) {
        when(consumers.getDollyBackend()).thenReturn(properties);
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> {
                    request.set(clientRequest);
                    return Mono.just(ClientResponse.create(status)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(body)
                            .build());
                })
                .build();
        return new DollyBackendConsumer(consumers, tokenExchange, webClient);
    }

    private static ServerProperties properties(String backend) {
        return ServerProperties.of("dev-gcp", "dolly", backend, "http://" + backend);
    }
}
