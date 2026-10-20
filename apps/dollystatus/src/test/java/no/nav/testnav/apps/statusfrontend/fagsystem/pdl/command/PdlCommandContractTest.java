package no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class PdlCommandContractTest {

    private static final String IDENT = "03458537037";
    private static final RunId RUN_ID = RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");

    @Test
    void shouldDecodeRawIdentFromCreateResponse() {
        var request = new AtomicReference<ClientRequest>();
        var webClient = webClient(request, IDENT);

        StepVerifier.create(new CreatePdlForvalterPersonCommand(
                        webClient,
                        "access-token",
                        IDENT,
                        Duration.ofSeconds(30)).call())
                .verifyComplete();

        assertThat(request.get().method().name()).isEqualTo("POST");
        assertThat(request.get().url().getPath()).isEqualTo("/api/v1/personer");
    }

    @Test
    void shouldDecodeRawIdentFromUpdateResponse() {
        var request = new AtomicReference<ClientRequest>();
        var webClient = webClient(request, IDENT);

        StepVerifier.create(new UpdatePdlForvalterPersonCommand(
                        webClient,
                        "access-token",
                        IDENT,
                        Duration.ofSeconds(30)).call())
                .verifyComplete();

        assertThat(request.get().method().name()).isEqualTo("PUT");
        assertThat(request.get().url().getPath()).isEqualTo("/api/v1/personer/03458537037");
        assertThat(request.get().headers().getFirst("relaxed")).isEqualTo("true");
    }

    @Test
    void shouldUseQ1GraphQlEndpointAndRequiredHeaders() {
        var request = new AtomicReference<ClientRequest>();
        var webClient = webClient(request, """
                {
                  "data": {
                    "hentPerson": {"navn": []},
                    "hentIdenter": {
                      "identer": [
                        {"ident": "03458537037", "historisk": false}
                      ]
                    }
                  }
                }
                """);

        StepVerifier.create(new VerifyPdlPersonCommand(
                        webClient,
                        "access-token",
                        IDENT,
                        FunctionalTestEnvironment.Q1,
                        RUN_ID,
                        Duration.ofSeconds(30)).call())
                .expectNext(true)
                .verifyComplete();

        assertThat(request.get().url().getPath()).isEqualTo("/pdl-api-q1/graphql");
        assertThat(request.get().headers().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer access-token");
        assertThat(request.get().headers().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(request.get().headers().getFirst("Tema")).isEqualTo("GEN");
        assertThat(request.get().headers().getFirst("Nav-Consumer-Id")).isEqualTo("Dollystatus");
        assertThat(request.get().headers().getFirst("Nav-Call-Id"))
                .isEqualTo("Dollystatus-" + RUN_ID.value());
        assertThat(request.get().headers().getFirst("Nav-Call-Id"))
                .doesNotContain("03458537037");
    }

    @Test
    void shouldUseQ2GraphQlEndpointAndRejectGraphQlErrors() {
        var request = new AtomicReference<ClientRequest>();
        var webClient = webClient(request, """
                {
                  "errors": [{"message": "Sensitive downstream detail"}],
                  "data": {
                    "hentPerson": {"navn": []},
                    "hentIdenter": {
                      "identer": [
                        {"ident": "03458537037", "historisk": false}
                      ]
                    }
                  }
                }
                """);

        StepVerifier.create(new VerifyPdlPersonCommand(
                        webClient,
                        "access-token",
                        IDENT,
                        FunctionalTestEnvironment.Q2,
                        RUN_ID,
                        Duration.ofSeconds(30)).call())
                .expectNext(false)
                .verifyComplete();

        assertThat(request.get().url().getPath()).isEqualTo("/pdl-api/graphql");
    }

    @Test
    void shouldParseNestedOrderEventStatuses() {
        var request = new AtomicReference<ClientRequest>();
        var webClient = webClient(request, """
                {
                  "hovedperson": {
                    "ident": "03458537037",
                    "ordrer": [
                      {
                        "ident": "03458537037",
                        "infoElement": "PDL_NAVN",
                        "hendelser": [
                          {
                            "id": 1,
                            "status": "FEIL",
                            "error": "Sensitive downstream detail"
                          }
                        ]
                      }
                    ]
                  },
                  "relasjoner": []
                }
                """);

        StepVerifier.create(new SendPdlOrderCommand(
                        webClient,
                        "access-token",
                        IDENT,
                        Duration.ofSeconds(30)).call())
                .assertNext(response -> assertThat(response.hovedperson()
                        .ordrer()
                        .getFirst()
                        .hendelser()
                        .getFirst()
                        .status()).isEqualTo("FEIL"))
                .verifyComplete();

        assertThat(request.get().method().name()).isEqualTo("POST");
        assertThat(request.get().url().getPath()).isEqualTo("/api/v1/personer/03458537037/ordre");
        assertThat(request.get().url().getQuery())
                .isEqualTo("ekskluderEksternePersoner=false");
    }

    @Test
    void shouldRequireExactPersonInPdlForvalterResponse() {
        var request = new AtomicReference<ClientRequest>();
        var webClient = webClient(request, """
                [
                  {
                    "person": {
                      "ident": "01478537038"
                    }
                  }
                ]
                """);

        StepVerifier.create(new GetPdlForvalterPersonCommand(
                        webClient,
                        "access-token",
                        IDENT,
                        Duration.ofSeconds(30)).call())
                .expectNext(false)
                .verifyComplete();

        assertThat(request.get().url().getPath()).isEqualTo("/api/v1/personer");
        assertThat(request.get().url().getQuery())
                .isEqualTo("identer=03458537037&sidenummer=0&pagesize=10");
    }

    private static WebClient webClient(AtomicReference<ClientRequest> request, String responseBody) {
        return WebClient.builder()
                .baseUrl("https://example.invalid")
                .exchangeFunction(clientRequest -> {
                    request.set(clientRequest);
                    return Mono.just(ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(responseBody)
                            .build());
                })
                .build();
    }
}
