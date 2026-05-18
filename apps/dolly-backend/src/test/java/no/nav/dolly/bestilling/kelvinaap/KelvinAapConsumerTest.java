package no.nav.dolly.bestilling.kelvinaap;

import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

class KelvinAapConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String SAKSNUMMER = "SAK-123456";

    @Autowired
    private KelvinAapConsumer kelvinAapConsumer;

    @Test
    void shouldCreateAapSuccessfully() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/opprettOgFullfoerBehandling"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"saksnummer": "%s"}
                                """.formatted(SAKSNUMMER))));

        var request = AapOpprettRequest.builder()
                .ident(IDENT)
                .erStudent(false)
                .harMedlemskap(true)
                .harYrkesskade(false)
                .build();

        StepVerifier.create(kelvinAapConsumer.createAap(request))
                .assertNext(response -> {
                    assertThat(response.getSaksnummer()).isEqualTo(SAKSNUMMER);
                    assertThat(response.getStatus()).isNull();
                    assertThat(response.getError()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleCreateAapBadRequest() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/opprettOgFullfoerBehandling"))
                .willReturn(badRequest()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Ugyldig forespørsel\"}")));

        var request = AapOpprettRequest.builder()
                .ident(IDENT)
                .build();

        StepVerifier.create(kelvinAapConsumer.createAap(request))
                .assertNext(response -> {
                    assertThat(response.getSaksnummer()).isNull();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getError()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleCreateAapServerError() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/opprettOgFullfoerBehandling"))
                .willReturn(serverError()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Intern serverfeil\"}")));

        var request = AapOpprettRequest.builder()
                .ident(IDENT)
                .build();

        StepVerifier.create(kelvinAapConsumer.createAap(request))
                .assertNext(response -> {
                    assertThat(response.getSaksnummer()).isNull();
                    assertThat(response.getStatus()).isNotNull();
                    assertThat(response.getError()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void shouldReadAapStatusSuccessfully() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/behandlingStatus"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "saksnummer": "%s",
                                    "behandlingStatus": "FERDIG",
                                    "ferdig": true,
                                    "soeknad": {
                                        "erStudent": false,
                                        "harYrkesskade": false,
                                        "harMedlemskap": true
                                    }
                                }
                                """.formatted(SAKSNUMMER))));

        StepVerifier.create(kelvinAapConsumer.readAap(IDENT))
                .assertNext(response -> {
                    assertThat(response.getSaksnummer()).isEqualTo(SAKSNUMMER);
                    assertThat(response.getBehandlingStatus()).isEqualTo("FERDIG");
                    assertThat(response.getFerdig()).isTrue();
                    assertThat(response.getSoeknad()).isNotNull();
                    assertThat(response.getSoeknad().getErStudent()).isFalse();
                    assertThat(response.getSoeknad().getHarMedlemskap()).isTrue();
                    assertThat(response.getStatus()).isNull();
                    assertThat(response.getError()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldReadAapStatusWithNoExistingBehandling() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/behandlingStatus"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));

        StepVerifier.create(kelvinAapConsumer.readAap(IDENT))
                .assertNext(response -> {
                    assertThat(response.getSoeknad()).isNull();
                    assertThat(response.getStatus()).isNull();
                    assertThat(response.getError()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleReadAapBadRequest() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/behandlingStatus"))
                .willReturn(badRequest()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Ukjent ident\"}")));

        StepVerifier.create(kelvinAapConsumer.readAap(IDENT))
                .assertNext(response -> {
                    assertThat(response.getSoeknad()).isNull();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getError()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleReadAapServerError() {

        stubFor(post(urlPathMatching("(.*)/kelvin-aap/api/test/behandlingStatus"))
                .willReturn(serverError()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Intern serverfeil\"}")));

        StepVerifier.create(kelvinAapConsumer.readAap(IDENT))
                .assertNext(response -> {
                    assertThat(response.getSoeknad()).isNull();
                    assertThat(response.getStatus()).isNotNull();
                    assertThat(response.getError()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnCorrectServiceUrl() {

        assertThat(kelvinAapConsumer.serviceUrl())
                .contains("localhost");
    }

    @Test
    void shouldReturnCorrectConsumerName() {

        assertThat(kelvinAapConsumer.consumerName())
                .isEqualTo("testnav-dolly-proxy");
    }
}
