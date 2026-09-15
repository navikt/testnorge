package no.nav.testnav.apps.brukerservice.consumer;

import no.nav.testnav.apps.brukerservice.config.Consumers;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DollyBackendConsumerTest {

    @Mock
    private Consumers consumers;

    @Mock
    private TokenExchange tokenExchange;

    @Test
    void shouldReturnRepresentingTeamFromAuthenticatedDollyUser() {
        var request = new AtomicReference<ClientRequest>();
        var consumer = createConsumer(request, """
                {
                  "representererTeam": {
                    "brukerId": "team-bruker-id-42"
                  }
                }
                """);

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectNext("team-bruker-id-42")
                .verifyComplete();

        assertThat(request.get().url().getPath()).isEqualTo("/api/v1/bruker/current");
        assertThat(request.get().headers().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer obo-token");
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotRepresentTeam() {
        var consumer = createConsumer(new AtomicReference<>(), """
                {
                  "representererTeam": null
                }
                """);

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .verifyComplete();
    }

    @Test
    void shouldRejectTeamWithoutSyntheticUserId() {
        var consumer = createConsumer(new AtomicReference<>(), """
                {
                  "representererTeam": {
                    "brukerId": " "
                  }
                }
                """);

        StepVerifier.create(consumer.getRepresentererTeamBrukerId())
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private DollyBackendConsumer createConsumer(
            AtomicReference<ClientRequest> request,
            String responseBody
    ) {
        var serviceProperties = ServerProperties.of(
                "dev-gcp",
                "dolly",
                "dolly-backend",
                "http://dolly-backend");
        when(consumers.getDollyBackend()).thenReturn(serviceProperties);
        when(tokenExchange.exchange(serviceProperties))
                .thenReturn(Mono.just(new AccessToken("obo-token")));

        ExchangeFunction exchangeFunction = clientRequest -> {
            request.set(clientRequest);
            return Mono.just(ClientResponse
                    .create(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(responseBody)
                    .build());
        };
        var webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
        return new DollyBackendConsumer(consumers, tokenExchange, webClient);
    }
}
