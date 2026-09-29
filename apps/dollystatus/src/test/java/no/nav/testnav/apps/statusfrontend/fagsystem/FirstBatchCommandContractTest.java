package no.nav.testnav.apps.statusfrontend.fagsystem;

import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command.CreateArbeidssoekerregistreringCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command.DeleteArbeidssoekerregistreringCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command.GetArbeidssoekerregistreringCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataEnvironments;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataRecord;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataSearchRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.CreateInstdataCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.DeleteInstdataCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.GetInstdataCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.GetInstdataEnvironmentsCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.tags.command.GetTagsCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command.CreateEgenansattCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command.DeleteEgenansattCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command.GetEgenansattCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DollyWireMockExtension.class)
class FirstBatchCommandContractTest {

    private static final String IDENT = "03458537037";
    private static final String TOKEN = "access-token";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:" + DollyWireMockExtension.getPort())
            .build();

    @Test
    void shouldUseArbeidssoekerregisteretContracts() {
        var request = arbeidssoekerRequest();
        stubFor(post(urlPathEqualTo("/api/v1/arbeidssoekerregistrering"))
                .willReturn(ok()));
        stubFor(get(urlPathEqualTo("/api/v1/arbeidssoekerregistrering/" + IDENT))
                .willReturn(okJson("""
                        {
                          "utfoertAv": "SLUTTBRUKER",
                          "kilde": "Dolly",
                          "aarsak": "Dollystatus-kontrakttest",
                          "nuskode": "4",
                          "jobbsituasjonsbeskrivelse": "HAR_SAGT_OPP"
                        }
                        """)));
        stubFor(delete(urlPathEqualTo("/api/v1/arbeidssoekerregistrering/" + IDENT))
                .willReturn(ok()));

        StepVerifier.create(new CreateArbeidssoekerregistreringCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetArbeidssoekerregistreringCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeleteArbeidssoekerregistreringCommand(
                        webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/api/v1/arbeidssoekerregistrering"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson("""
                        {
                          "identitetsnummer": "03458537037",
                          "utfoertAv": "SLUTTBRUKER",
                          "kilde": "Dolly",
                          "aarsak": "Dollystatus-kontrakttest",
                          "nuskode": "4",
                          "utdanningBestaatt": true,
                          "utdanningGodkjent": true,
                          "jobbsituasjonsbeskrivelse": "HAR_SAGT_OPP",
                          "jobbsituasjonsdetaljer": {
                            "gjelderFraDato": "2025-01-01",
                            "gjelderTilDato": "2025-01-31",
                            "stillingStyrk08": 2522,
                            "stillingstittel": "Programvareutvikler",
                            "stillingsprosent": 100,
                            "sisteDagMedLoenn": "2025-01-31",
                            "sisteArbeidsdag": "2025-01-30"
                          },
                          "helsetilstandHindrerArbeid": false,
                          "andreForholdHindrerArbeid": false
                        }
                        """)));
        verify(getRequestedFor(urlPathEqualTo("/api/v1/arbeidssoekerregistrering/" + IDENT))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
        verify(deleteRequestedFor(urlPathEqualTo("/api/v1/arbeidssoekerregistrering/" + IDENT))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
    }

    @Test
    void shouldTreatMissingArbeidssoekerregistreringAsEmpty() {
        stubFor(get(urlPathEqualTo("/api/v1/arbeidssoekerregistrering/" + IDENT))
                .willReturn(aResponse().withStatus(404)));

        StepVerifier.create(new GetArbeidssoekerregistreringCommand(
                        webClient, TOKEN, arbeidssoekerRequest(), TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isTrue();
                    assertThat(status.expectedDataPresent()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void shouldUseInstdataContractsAndResponseBodies() {
        var record = instdataRecord();
        var searchRequest = new InstdataSearchRequest(IDENT, List.of("q2"));
        stubFor(get(urlPathEqualTo("/inst/api/v1/environment"))
                .willReturn(okJson("""
                        {
                          "institusjonsoppholdEnvironments": ["q1", "q2"],
                          "kdiEnvironments": ["q2"]
                        }
                        """)));
        stubFor(post(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person/soek"))
                .willReturn(okJson("""
                        {
                          "q2": [{
                            "norskident": "03458537037",
                            "tssEksternId": "80000464106",
                            "institusjonstype": "AS",
                            "oppholdstype": "A",
                            "startdato": "2025-01-01",
                            "sluttdato": "2025-01-03",
                            "forventetSluttdato": "2025-01-03",
                            "registrertAv": "Dolly"
                          }]
                        }
                        """)));
        stubFor(post(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person"))
                .willReturn(ok()));
        stubFor(post(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person/slett"))
                .willReturn(ok()));

        StepVerifier.create(new GetInstdataEnvironmentsCommand(webClient, TOKEN, TIMEOUT).call())
                .assertNext(environments -> assertThat(environments)
                        .isEqualTo(new InstdataEnvironments(List.of("q1", "q2"), List.of("q2"))))
                .verifyComplete();
        StepVerifier.create(new GetInstdataCommand(
                        webClient, TOKEN, searchRequest, record, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new CreateInstdataCommand(
                        webClient, TOKEN, "q2", record, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new DeleteInstdataCommand(
                        webClient, TOKEN, searchRequest, TIMEOUT).call())
                .verifyComplete();

        verify(getRequestedFor(urlPathEqualTo("/inst/api/v1/environment"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
        verify(postRequestedFor(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person/soek"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson("""
                        {"personident":"03458537037","environments":["q2"]}
                        """)));
        verify(postRequestedFor(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withQueryParam("environments", equalTo("q2"))
                .withRequestBody(equalToJson("""
                        {
                          "norskident": "03458537037",
                          "tssEksternId": "80000464106",
                          "institusjonstype": "AS",
                          "oppholdstype": "A",
                          "startdato": "2025-01-01",
                          "sluttdato": "2025-01-03",
                          "forventetSluttdato": "2025-01-03",
                          "registrertAv": "Dolly"
                        }
                        """)));
        verify(postRequestedFor(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person/slett"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson("""
                        {"personident":"03458537037","environments":["q2"]}
                        """)));
    }

    @Test
    void shouldTreatMissingInstdataEnvironmentAsEmpty() {
        var searchRequest = new InstdataSearchRequest(IDENT, List.of("q2"));
        stubFor(post(urlPathEqualTo("/inst/api/v1/institusjonsopphold/person/soek"))
                .willReturn(okJson("{}")));

        StepVerifier.create(new GetInstdataCommand(
                        webClient, TOKEN, searchRequest, instdataRecord(), TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isTrue();
                    assertThat(status.expectedDataPresent()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void shouldUseTpsEgenansattContractsAndValidateBothEnvironments() {
        var environments = List.of("q1", "q2");
        var fromDate = LocalDate.of(2025, 1, 1);
        stubFor(post(urlPathEqualTo("/api/v1/personer/" + IDENT + "/egenansatt"))
                .willReturn(okJson(tpsStatusResponse())));
        stubFor(post(urlPathEqualTo("/api/v1/personer/ident"))
                .willReturn(okJson("""
                        [
                          {
                            "miljoe": "q1",
                            "status": "OK",
                            "person": {
                              "egenAnsattDatoFom": "2025-01-01T00:00:00"
                            }
                          },
                          {
                            "miljoe": "q2",
                            "status": "OK",
                            "person": {
                              "egenAnsattDatoFom": "2025-01-01T00:00:00"
                            }
                          }
                        ]
                        """)));
        stubFor(delete(urlPathEqualTo("/api/v1/personer/" + IDENT + "/egenansatt"))
                .willReturn(okJson(tpsStatusResponse())));

        StepVerifier.create(new CreateEgenansattCommand(
                        webClient, TOKEN, IDENT, environments, fromDate, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetEgenansattCommand(
                        webClient, TOKEN, IDENT, environments, fromDate, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.allEnvironmentsPresent()).isTrue();
                    assertThat(status.expectedDataPresent()).isTrue();
                    assertThat(status.inactive()).isFalse();
                    assertThat(status.hasStartDate()).isTrue();
                    assertThat(status.hasEndDate()).isFalse();
                    assertThat(status.activeWithDifferentStartDate()).isFalse();
                })
                .verifyComplete();
        StepVerifier.create(new DeleteEgenansattCommand(
                        webClient, TOKEN, IDENT, environments, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/api/v1/personer/" + IDENT + "/egenansatt"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Content-Type", equalTo("application/json"))
                .withQueryParam("fraOgMed", equalTo("2025-01-01"))
                .withQueryParam("miljoer", equalTo("q1"))
                .withQueryParam("miljoer", equalTo("q2")));
        verify(postRequestedFor(urlPathEqualTo("/api/v1/personer/ident"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Content-Type", equalTo("application/json"))
                .withQueryParam("miljoer", equalTo("q1"))
                .withQueryParam("miljoer", equalTo("q2"))
                .withRequestBody(equalToJson("""
                        {"ident":"03458537037"}
                        """)));
        verify(deleteRequestedFor(urlPathEqualTo("/api/v1/personer/" + IDENT + "/egenansatt"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withQueryParam("miljoer", equalTo("q1"))
                .withQueryParam("miljoer", equalTo("q2")));
    }

    @Test
    void shouldUseTagsReadContractWithoutPublishingOrDeleting() {
        stubFor(get(urlPathEqualTo("/pdl-testdata/api/v1/bestilling/tags"))
                .willReturn(okJson("""
                        {"tags":[]}
                        """)));

        StepVerifier.create(new GetTagsCommand(webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(getRequestedFor(urlPathEqualTo("/pdl-testdata/api/v1/bestilling/tags"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Personident", equalTo(IDENT)));
    }

    @Test
    void shouldDistinguishDifferentTpsStartDateFromMissingEnvironment() {
        stubFor(post(urlPathEqualTo("/api/v1/personer/ident"))
                .willReturn(okJson("""
                        [{"miljoe":"q1","status":"OK","person":{"egenAnsattDatoFom":"2025-01-01T00:00:00"}}]
                        """)));

        StepVerifier.create(new GetEgenansattCommand(webClient, TOKEN, IDENT, List.of("q1"),
                        LocalDate.of(2025, 1, 2), TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.allEnvironmentsPresent()).isTrue();
                    assertThat(status.expectedDataPresent()).isFalse();
                    assertThat(status.inactive()).isFalse();
                    assertThat(status.hasStartDate()).isTrue();
                    assertThat(status.hasEndDate()).isFalse();
                    assertThat(status.activeWithDifferentStartDate()).isTrue();
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "{}|TPS_INVALID_RESPONSE",
            "[]|TPS_INCOMPLETE_ENVIRONMENT_STATUS",
            "[{\"miljoe\":\"q1\",\"status\":\"OK\"}]|TPS_INCOMPLETE_ENVIRONMENT_STATUS",
            "[{\"miljoe\":\"q1\",\"status\":\"OK\"},{\"miljoe\":\"q2\",\"status\":\"FEIL\",\"utfyllendeMelding\":\"sensitive-detail\"}]|TPS_ENVIRONMENT_FAILURE",
            "[{\"miljoe\":\"q1\",\"status\":\"FEIL\"},{\"miljoe\":\"q2\",\"status\":\"OK\"}]|TPS_ENVIRONMENT_FAILURE",
            "[{\"miljoe\":\"q1\",\"status\":\"OK\"},{\"miljoe\":\"q2\"}]|TPS_ENVIRONMENT_FAILURE"
    }, delimiter = '|')
    void shouldDescribeTpsMutationResponseFailuresWithoutResponseValues(String body, Reason expectedReason) {
        var path = "/api/v1/personer/" + IDENT + "/egenansatt";
        var environments = List.of("q1", "q2");
        stubFor(post(urlPathEqualTo(path)).willReturn(okJson(body)));
        stubFor(delete(urlPathEqualTo(path)).willReturn(okJson(body)));

        var operations = List.of(
                new CreateEgenansattCommand(webClient, TOKEN, IDENT, environments,
                        LocalDate.of(2025, 1, 1), TIMEOUT).call(),
                new DeleteEgenansattCommand(webClient, TOKEN, IDENT, environments, TIMEOUT).call());
        operations.forEach(operation -> StepVerifier.create(operation)
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOfSatisfying(FunctionalTestResponseException.class, failure -> {
                            assertThat(failure.reason()).isEqualTo(expectedReason);
                            assertThat(failure.getMessage()).isEqualTo(expectedReason.name());
                            assertThat(failure.getCause()).isNull();
                        }))
                .verify());
    }

    @Test
    void shouldDistinguishInvalidTpsLookupFromMutationResponse() {
        stubFor(post(urlPathEqualTo("/api/v1/personer/ident"))
                .willReturn(okJson("{\"melding\":\"sensitive-detail\"}")));

        StepVerifier.create(new GetEgenansattCommand(webClient, TOKEN, IDENT, List.of("q1", "q2"),
                        LocalDate.of(2025, 1, 1), TIMEOUT).call())
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOfSatisfying(FunctionalTestResponseException.class,
                                failure -> assertThat(failure.reason()).isEqualTo(Reason.TPS_LOOKUP_INVALID_RESPONSE)))
                .verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[{\"miljoe\":\"q2\",\"status\":\"OK\"}]",
            "[{\"miljoe\":\"q2\",\"status\":\"OK\",\"person\":null}]",
            "[{\"miljoe\":\"q2\",\"status\":\"OK\",\"person\":[]}]"
    })
    void shouldRejectMissingTpsPersonInsteadOfReportingInactive(String body) {
        stubFor(post(urlPathEqualTo("/api/v1/personer/ident")).willReturn(okJson(body)));

        StepVerifier.create(new GetEgenansattCommand(webClient, TOKEN, IDENT, List.of("q2"),
                        LocalDate.of(2025, 1, 1), TIMEOUT).call())
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOfSatisfying(FunctionalTestResponseException.class,
                                failure -> assertThat(failure.reason()).isEqualTo(Reason.TPS_LOOKUP_INVALID_RESPONSE)))
                .verify();
    }

    @Test
    void shouldRejectEmptyTpsLookupAndMutationResponses() {
        var path = "/api/v1/personer/" + IDENT + "/egenansatt";
        var environments = List.of("q2");
        var fromDate = LocalDate.of(2025, 1, 1);
        stubFor(post(urlPathEqualTo("/api/v1/personer/ident")).willReturn(ok()));
        stubFor(post(urlPathEqualTo(path)).willReturn(ok()));
        stubFor(delete(urlPathEqualTo(path)).willReturn(ok()));

        var operations = List.of(
                new GetEgenansattCommand(webClient, TOKEN, IDENT, environments, fromDate, TIMEOUT).call(),
                new CreateEgenansattCommand(webClient, TOKEN, IDENT, environments, fromDate, TIMEOUT).call(),
                new DeleteEgenansattCommand(webClient, TOKEN, IDENT, environments, TIMEOUT).call());
        operations.forEach(operation -> StepVerifier.create(operation)
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOfSatisfying(FunctionalTestResponseException.class,
                                failure -> assertThat(failure.reason()).isEqualTo(Reason.TPS_EMPTY_RESPONSE)))
                .verify());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2"})
    void shouldSendTpsMutationsOnlyToSelectedEnvironment(String environment) {
        var path = "/api/v1/personer/" + IDENT + "/egenansatt";
        var environments = List.of(environment);
        var response = "[{\"miljoe\":\"" + environment + "\",\"status\":\"OK\"}]";
        stubFor(post(urlPathEqualTo(path)).willReturn(okJson(response)));
        stubFor(delete(urlPathEqualTo(path)).willReturn(okJson(response)));

        StepVerifier.create(new CreateEgenansattCommand(webClient, TOKEN, IDENT, environments,
                LocalDate.of(2025, 1, 1), TIMEOUT).call()).verifyComplete();
        StepVerifier.create(new DeleteEgenansattCommand(webClient, TOKEN, IDENT, environments, TIMEOUT).call())
                .verifyComplete();

        var requests = findAll(anyRequestedFor(urlPathEqualTo(path)));
        assertThat(requests).hasSize(2).allSatisfy(request ->
                assertThat(request.queryParameter("miljoer").values()).containsExactly(environment));
    }

    private static ArbeidssoekerregisteretRequest arbeidssoekerRequest() {
        return new ArbeidssoekerregisteretRequest(
                IDENT,
                "SLUTTBRUKER",
                "Dolly",
                "Dollystatus-kontrakttest",
                "4",
                true,
                true,
                "HAR_SAGT_OPP",
                new ArbeidssoekerregisteretRequest.Jobbsituasjonsdetaljer(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 1, 31),
                        2522,
                        "Programvareutvikler",
                        100,
                        LocalDate.of(2025, 1, 31),
                        LocalDate.of(2025, 1, 30)),
                false,
                false);
    }

    private static InstdataRecord instdataRecord() {
        return new InstdataRecord(
                IDENT,
                "80000464106",
                "AS",
                "A",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 3),
                LocalDate.of(2025, 1, 3),
                "Dolly");
    }

    private static String tpsStatusResponse() {
        return """
                [
                  {"miljoe":"q1","status":"OK"},
                  {"miljoe":"q2","status":"OK"}
                ]
                """;
    }

    private static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder okJson(String body) {
        return ok()
                .withHeader("Content-Type", "application/json")
                .withBody(body);
    }
}
