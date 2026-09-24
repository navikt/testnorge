package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreateAfpOffentligCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreatePensjonsavtaleCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreatePoppCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreateTpForholdCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeleteAfpOffentligCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeletePensjonsavtaleCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeletePoppCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeleteTpForholdCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetAfpOffentligCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetPensjonsavtaleCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetPoppCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetTpForholdCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DollyWireMockExtension.class)
class PensjonCommandContractTest {

    private static final String IDENT = "03458537037";
    private static final String TOKEN = "access-token";
    private static final RunId RUN_ID = RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:" + DollyWireMockExtension.getPort())
            .build();

    @Test
    void shouldUseTpEndpointsHeadersQueryAndPayload() {
        stubFor(post(urlPathEqualTo("/pensjon/api/v1/tp/forhold"))
                .willReturn(okJson(successResponse("q1"))));
        stubFor(get(urlPathEqualTo("/pensjon/api/v1/tp/forhold"))
                .willReturn(okJson("""
                        [{"ordning":"3010"}]
                        """)));
        stubFor(delete(urlPathEqualTo("/pensjon/api/v1/tp/person/forhold"))
                .willReturn(okJson(successResponse("q1"))));

        var request = PensjonTestData.tpRequest(IDENT, FunctionalTestEnvironment.Q1);

        StepVerifier.create(new CreateTpForholdCommand(
                        webClient, TOKEN, RUN_ID, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetTpForholdCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", "3010", TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeleteTpForholdCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/pensjon/api/v1/tp/forhold"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo("Dollystatus-" + RUN_ID.value()))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly"))
                .withRequestBody(equalToJson("""
                        {
                          "miljoer": ["q1"],
                          "fnr": "03458537037",
                          "ordning": "3010"
                        }
                        """)));
        verify(getRequestedFor(urlPathEqualTo("/pensjon/api/v1/tp/forhold"))
                .withQueryParam("fnr", equalTo(IDENT))
                .withQueryParam("miljo", equalTo("q1")));
        verify(deleteRequestedFor(urlPathEqualTo("/pensjon/api/v1/tp/person/forhold"))
                .withQueryParam("miljoer", equalTo("q1"))
                .withHeader("pid", equalTo(IDENT)));
    }

    @Test
    void shouldUsePoppEndpointsQueriesAndPayload() {
        stubFor(post(urlPathEqualTo("/pensjon/api/v1/inntekt"))
                .willReturn(okJson(successResponse("q2"))));
        stubFor(get(urlPathEqualTo("/pensjon/api/v1/inntekt"))
                .willReturn(okJson("""
                        {"inntekter":[{"InntektAar":2020,"belop":12345}]}
                        """)));
        stubFor(delete(urlPathEqualTo("/pensjon/api/v1/popp/person"))
                .willReturn(okJson(successResponse("q2"))));

        var request = PensjonTestData.poppRequest(IDENT, FunctionalTestEnvironment.Q2);

        StepVerifier.create(new CreatePoppCommand(
                        webClient, TOKEN, RUN_ID, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetPoppCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q2", 2020, 12345, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeletePoppCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q2", TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/pensjon/api/v1/inntekt"))
                .withRequestBody(equalToJson("""
                        {
                          "fnr": "03458537037",
                          "tomAar": 2020,
                          "fomAar": 2020,
                          "belop": 12345,
                          "redusertMedGrunnbelop": false,
                          "miljoer": ["q2"]
                        }
                        """)));
        verify(getRequestedFor(urlPathEqualTo("/pensjon/api/v1/inntekt"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo("Dollystatus-" + RUN_ID.value()))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly"))
                .withQueryParam("fnr", equalTo(IDENT))
                .withQueryParam("miljo", equalTo("q2")));
        verify(deleteRequestedFor(urlPathEqualTo("/pensjon/api/v1/popp/person"))
                .withQueryParam("miljoer", equalTo("q2"))
                .withHeader("pid", equalTo(IDENT)));
    }

    @Test
    void shouldUseAfpEndpointsAndPayload() {
        stubFor(put(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT))
                .willReturn(ok()));
        stubFor(get(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT))
                .willReturn(okJson("""
                        {
                          "direktekall": [],
                          "mocksvar": [{"tpId":"4099","statusAfp":"INNVILGET"}]
                        }
                        """)));
        stubFor(delete(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT))
                .willReturn(ok()));

        var request = PensjonTestData.afpRequest(IDENT);

        StepVerifier.create(new CreateAfpOffentligCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetAfpOffentligCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", "4099", TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeleteAfpOffentligCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", TIMEOUT).call())
                .verifyComplete();

        verify(putRequestedFor(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT))
                .withRequestBody(equalToJson("""
                        {
                          "direktekall": [],
                          "mocksvar": [{
                            "tpId": "4099",
                            "fnr": "03458537037",
                            "statusAfp": "INNVILGET",
                            "virkningsDato": "2025-01-01",
                            "sistBenyttetG": 2025,
                            "belopsListe": [{
                              "fomDato": "2025-01-01",
                              "belop": 10000
                            }]
                          }]
                        }
                        """)));
        verify(getRequestedFor(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo("Dollystatus-" + RUN_ID.value()))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly")));
        verify(deleteRequestedFor(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT)));
    }

    @Test
    void shouldUseGlobalPensjonsavtaleEndpointsAndPayload() {
        stubFor(post(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/opprett"))
                .willReturn(okJson(successResponse("q1", "q2"))));
        stubFor(get(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/hent"))
                .willReturn(okJson("""
                        [{
                          "produktbetegnelse": "Dollystatus syntetisk kontrakttest",
                          "kategori": "PRIVAT_TJENESTEPENSJON"
                        }]
                        """)));
        stubFor(delete(urlPathEqualTo("/pensjon/api/v1/pensjonsavtale/delete"))
                .willReturn(okJson(successResponse("q1", "q2"))));

        var request = PensjonTestData.pensjonsavtaleRequest(IDENT);

        StepVerifier.create(new CreatePensjonsavtaleCommand(
                        webClient, TOKEN, RUN_ID, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetPensjonsavtaleCommand(
                        webClient,
                        TOKEN,
                        RUN_ID,
                        IDENT,
                        "q1",
                        "Dollystatus syntetisk kontrakttest",
                        TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeletePensjonsavtaleCommand(
                        webClient, TOKEN, RUN_ID, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/opprett"))
                .withRequestBody(equalToJson("""
                        {
                          "ident": "03458537037",
                          "produktBetegnelse": "Dollystatus syntetisk kontrakttest",
                          "avtaleKategori": "PRIVAT_TJENESTEPENSJON",
                          "utbetalingsperioder": [{
                            "startAlderAar": 62,
                            "startAlderMaaned": 1,
                            "sluttAlderAar": 72,
                            "sluttAlderMaaned": 12,
                            "aarligUtbetaling": 30000
                          }],
                          "miljoer": ["q1", "q2"]
                        }
                        """)));
        verify(getRequestedFor(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/hent"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo("Dollystatus-" + RUN_ID.value()))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly"))
                .withQueryParam("miljo", equalTo("q1"))
                .withHeader("ident", equalTo(IDENT)));
        verify(deleteRequestedFor(urlPathEqualTo("/pensjon/api/v1/pensjonsavtale/delete"))
                .withHeader("ident", equalTo(IDENT)));
    }

    @Test
    void shouldRejectIncompleteNestedEnvironmentStatus() {
        stubFor(post(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/opprett"))
                .willReturn(okJson(successResponse("q1"))));

        var request = PensjonTestData.pensjonsavtaleRequest(IDENT);

        StepVerifier.create(new CreatePensjonsavtaleCommand(
                        webClient, TOKEN, RUN_ID, request, TIMEOUT).call())
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && error.getMessage().equals("Pensjon-operasjonen returnerte ugyldig miljøstatus."))
                .verify();
    }

    @Test
    void shouldTreatNotFoundLookupAsEmpty() {
        stubFor(get(urlPathEqualTo("/pensjon/api/v1/tp/forhold"))
                .willReturn(aResponse().withStatus(404)));

        StepVerifier.create(new GetTpForholdCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", "3010", TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isTrue();
                    assertThat(status.expectedDataPresent()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void shouldTreatNotFoundAndEmptyPoppLookupsAsEmpty() {
        stubFor(get(urlPathEqualTo("/pensjon/api/v1/inntekt"))
                .withQueryParam("miljo", equalTo("q1"))
                .willReturn(aResponse().withStatus(404)));
        stubFor(get(urlPathEqualTo("/pensjon/api/v1/inntekt"))
                .withQueryParam("miljo", equalTo("q2"))
                .willReturn(ok()));

        StepVerifier.create(new GetPoppCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", 2020, 12345, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetPoppCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q2", 2020, 12345, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldTreatNotFoundAndEmptyAfpLookupsAsEmpty() {
        stubFor(get(urlPathEqualTo("/pensjon/q1/api/mock-oppsett/" + IDENT))
                .willReturn(aResponse().withStatus(404)));
        stubFor(get(urlPathEqualTo("/pensjon/q2/api/mock-oppsett/" + IDENT))
                .willReturn(ok()));

        StepVerifier.create(new GetAfpOffentligCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", "4099", TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetAfpOffentligCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q2", "4099", TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldTreatNotFoundAndEmptyPensjonsavtaleLookupsAsEmpty() {
        stubFor(get(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/hent"))
                .withQueryParam("miljo", equalTo("q1"))
                .willReturn(aResponse().withStatus(404)));
        stubFor(get(urlPathEqualTo("/pensjon/api/v2/pensjonsavtale/hent"))
                .withQueryParam("miljo", equalTo("q2"))
                .willReturn(ok()));

        StepVerifier.create(new GetPensjonsavtaleCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q1", "produkt", TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetPensjonsavtaleCommand(
                        webClient, TOKEN, RUN_ID, IDENT, "q2", "produkt", TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    private static String successResponse(String... environments) {
        return """
                {"status":[%s]}
                """.formatted(java.util.Arrays.stream(environments)
                .map(environment -> """
                        {"miljo":"%s","response":{"httpStatus":{"status":200,"reasonPhrase":"OK"}}}
                        """.formatted(environment).trim())
                .collect(java.util.stream.Collectors.joining(",")));
    }

    private static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder okJson(String body) {
        return ok()
                .withHeader("Content-Type", "application/json")
                .withBody(body);
    }
}
