package no.nav.testnav.apps.statusfrontend.fagsystem;

import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.command.CreateArenaUserCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.command.DeactivateArenaUserCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.command.GetArenaUserCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command.CreateKontoregisterAccountCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command.DeleteKontoregisterAccountCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command.GetKontoregisterAccountCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.command.CreateKrrContactInformationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.command.DeleteKrrContactInformationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.command.GetKrrContactInformationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.command.CloseNomResourceCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.command.CreateNomResourceCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.command.GetNomResourceCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortData;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.command.CreateSkattekortCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.command.GetSkattekortCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DollyWireMockExtension.class)
class SecondBatchCommandContractTest {

    private static final String IDENT = "03458537037";
    private static final String TOKEN = "access-token";
    private static final String CALL_ID = "aaf62d6f-eb87-49ce-bcef-b82ca3fd940d";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:" + DollyWireMockExtension.getPort())
            .build();

    @Test
    void shouldUseArenaContractsAndValidateResponseBody() {
        var request = new ArenaRequest(List.of(new ArenaRequest.User(
                IDENT,
                "q1",
                LocalDate.of(2026, 9, 21),
                "IKVAL",
                true)));
        stubFor(post(urlPathEqualTo("/arena/api/v1/bruker"))
                .willReturn(okJson("""
                        {"arbeidsokerList":[{"status":"OK"}],"nyBrukerFeilList":[]}
                        """)));
        stubFor(get(urlPathEqualTo(
                "/arena/q1/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .willReturn(okJson("""
                        {
                          "registrertDato": "2026-09-21",
                          "sistInaktivDato": "2026-09-20",
                          "formidlingsgruppe": {"kode": "ARBS"},
                          "servicegruppe": {"kode": "IKVAL"}
                        }
                        """)));
        stubFor(delete(urlPathEqualTo("/arena/api/v1/bruker"))
                .willReturn(ok()));

        StepVerifier.create(new CreateArenaUserCommand(
                        webClient, TOKEN, request, CALL_ID, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetArenaUserCommand(
                        webClient, TOKEN, IDENT, "q1", request, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.active()).isTrue();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeactivateArenaUserCommand(
                        webClient, TOKEN, IDENT, "q1", CALL_ID, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/arena/api/v1/bruker"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo(CALL_ID))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly"))
                .withRequestBody(equalToJson("""
                        {
                          "nyeBrukere": [{
                            "personident": "03458537037",
                            "miljoe": "q1",
                            "aktiveringsDato": "2026-09-21",
                            "kvalifiseringsgruppe": "IKVAL",
                            "automatiskInnsendingAvMeldekort": true
                          }]
                        }
                        """)));
        verify(getRequestedFor(urlPathEqualTo(
                        "/arena/q1/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("fodselsnr", equalTo(IDENT)));
        verify(deleteRequestedFor(urlPathEqualTo("/arena/api/v1/bruker"))
                .withQueryParam("miljoe", equalTo("q1"))
                .withQueryParam("personident", equalTo(IDENT))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo(CALL_ID))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly")));
    }

    @Test
    void shouldRejectArenaCreateWithoutSuccessfulResponseBody() {
        var request = new ArenaRequest(List.of(new ArenaRequest.User(
                IDENT,
                "q2",
                LocalDate.of(2026, 9, 21),
                "IKVAL",
                true)));
        stubFor(post(urlPathEqualTo("/arena/api/v1/bruker"))
                .willReturn(okJson("""
                        {"arbeidsokerList":[],"nyBrukerFeilList":[{"status":"FEIL"}]}
                        """)));

        StepVerifier.create(new CreateArenaUserCommand(
                        webClient, TOKEN, request, CALL_ID, TIMEOUT).call())
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && error.getMessage().contains("ugyldig respons"))
                .verify();
    }

    @ParameterizedTest
    @CsvSource({"ARBS,true,false", "ISERV,false,true", "'',false,false"})
    void shouldDetermineArenaActivityFromCurrentPlacementGroup(
            String placementGroup, boolean active, boolean inactive) {
        var request = new ArenaRequest(List.of(new ArenaRequest.User(
                IDENT, "q1", LocalDate.of(2026, 9, 21), "IKVAL", true)));
        stubFor(get(urlPathEqualTo(
                "/arena/q1/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .willReturn(okJson("""
                        {
                          "registrertDato": "2026-09-21",
                          "sistInaktivDato": null,
                          "formidlingsgruppe": {"kode": "%s"},
                          "servicegruppe": {"kode": "IKVAL"}
                        }
                        """.formatted(placementGroup))));

        StepVerifier.create(new GetArenaUserCommand(
                        webClient, TOKEN, IDENT, "q1", request, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.active()).isEqualTo(active);
                    assertThat(status.inactive()).isEqualTo(inactive);
                    assertThat(status.expectedDataPresent()).isEqualTo(active);
                })
                .verifyComplete();
    }

    @Test
    void shouldUseKontoregisterContractsAndTreatMissingAccountAsEmpty() {
        var account = new OppdaterKontoRequestDTO(IDENT, "12345678903", "Dolly", null);
        stubFor(post(urlPathEqualTo("/kontoregister/api/system/v1/oppdater-konto"))
                .willReturn(ok()));
        stubFor(post(urlPathEqualTo("/kontoregister/api/system/v1/hent-aktiv-konto"))
                .willReturn(okJson("""
                        {
                          "kontohaver": "03458537037",
                          "kontonummer": "12345678903",
                          "gyldigTom": null
                        }
                        """)));
        stubFor(post(urlPathEqualTo("/kontoregister/api/system/v1/slett-konto"))
                .willReturn(ok()));

        StepVerifier.create(new CreateKontoregisterAccountCommand(
                        webClient, TOKEN, account, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetKontoregisterAccountCommand(
                        webClient, TOKEN, account, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new DeleteKontoregisterAccountCommand(
                        webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo(
                        "/kontoregister/api/system/v1/oppdater-konto"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson("""
                        {
                          "kontohaver": "03458537037",
                          "kontonummer": "12345678903",
                          "opprettetAv": "Dolly"
                        }
                        """)));
        verify(postRequestedFor(urlPathEqualTo(
                        "/kontoregister/api/system/v1/hent-aktiv-konto"))
                .withRequestBody(equalToJson("""
                        {"kontohaver":"03458537037"}
                        """)));
        verify(postRequestedFor(urlPathEqualTo(
                        "/kontoregister/api/system/v1/slett-konto"))
                .withRequestBody(equalToJson("""
                        {"kontohaver":"03458537037","bestiller":"Dolly"}
                        """)));

        stubFor(post(urlPathEqualTo("/kontoregister/api/system/v1/hent-aktiv-konto"))
                .willReturn(aResponse().withStatus(404)));
        StepVerifier.create(new GetKontoregisterAccountCommand(
                        webClient, TOKEN, account, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldUseKrrContractsAndVerifyCleanup() {
        var timestamp = ZonedDateTime.of(
                2026, 9, 21, 10, 0, 0, 0, ZoneOffset.UTC);
        var request = new KrrRequest(
                IDENT,
                timestamp,
                false,
                true,
                "+4740000000",
                "dollystatus@example.invalid",
                "nb",
                timestamp,
                timestamp,
                timestamp,
                timestamp,
                timestamp,
                timestamp);
        stubFor(post(urlPathEqualTo("/krrstub/api/v2/kontaktinformasjon"))
                .willReturn(ok()));
        stubFor(post(urlPathEqualTo("/krrstub/api/v2/person/kontaktinformasjon/soek"))
                .willReturn(okJson("""
                        [{
                          "reservert": false,
                          "registrert": true,
                          "mobil": "+4740000000",
                          "epost": "dollystatus@example.invalid",
                          "spraak": "nb"
                        }]
                        """)));
        stubFor(delete(urlPathEqualTo("/krrstub/api/v2/person/kontaktinformasjon"))
                .willReturn(ok()));

        StepVerifier.create(new CreateKrrContactInformationCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetKrrContactInformationCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.expectedDataPresent()).isTrue())
                .verifyComplete();
        StepVerifier.create(new DeleteKrrContactInformationCommand(
                        webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/krrstub/api/v2/kontaktinformasjon"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly"))
                .withRequestBody(equalToJson("""
                        {
                          "personident": "03458537037",
                          "gyldigFra": "2026-09-21T10:00:00Z",
                          "reservert": false,
                          "registrert": true,
                          "mobil": "+4740000000",
                          "epost": "dollystatus@example.invalid",
                          "spraak": "nb",
                          "epostOppdatert": "2026-09-21T10:00:00Z",
                          "epostVerifisert": "2026-09-21T10:00:00Z",
                          "mobilOppdatert": "2026-09-21T10:00:00Z",
                          "mobilVerifisert": "2026-09-21T10:00:00Z",
                          "spraakOppdatert": "2026-09-21T10:00:00Z",
                          "reservertOppdatert": "2026-09-21T10:00:00Z"
                        }
                        """)));
        verify(postRequestedFor(urlPathEqualTo(
                        "/krrstub/api/v2/person/kontaktinformasjon/soek"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("""
                        {"personidentifikator":"03458537037"}
                        """)));
        verify(deleteRequestedFor(urlPathEqualTo(
                        "/krrstub/api/v2/person/kontaktinformasjon"))
                .withRequestBody(equalToJson("""
                        {"personidentifikator":"03458537037"}
                        """)));

        stubFor(post(urlPathEqualTo("/krrstub/api/v2/person/kontaktinformasjon/soek"))
                .willReturn(okJson("[]")));
        StepVerifier.create(new GetKrrContactInformationCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldUseNomContractsAndVerifyTerminatedResourceId() {
        var request = new NomRequest(
                IDENT,
                "Testesen",
                "Test",
                null,
                LocalDate.of(2026, 9, 21),
                null);
        stubFor(post(urlPathEqualTo("/api/v1/dolly/opprettRessurs"))
                .willReturn(ok()));
        stubFor(post(urlPathEqualTo("/api/v1/dolly/hentRessurs"))
                .willReturn(okJson("""
                        {
                          "fid": "12345",
                          "personident": "03458537037",
                          "navn": {"fornavn": "Test", "etternavn": "Testesen"},
                          "startDato": "2026-09-21",
                          "sluttDato": null
                        }
                        """)));
        stubFor(post(urlPathEqualTo("/api/v1/dolly/avsluttRessurs"))
                .willReturn(ok()));

        StepVerifier.create(new CreateNomResourceCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetNomResourceCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.expectedDataPresent()).isTrue();
                    assertThat(status.resourceId()).isEqualTo("12345");
                })
                .verifyComplete();
        StepVerifier.create(new CloseNomResourceCommand(
                        webClient,
                        TOKEN,
                        IDENT,
                        LocalDate.of(2026, 9, 21),
                        TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/api/v1/dolly/hentRessurs"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalTo(IDENT)));
        verify(postRequestedFor(urlPathEqualTo("/api/v1/dolly/opprettRessurs"))
                .withRequestBody(equalToJson("""
                        {
                          "personident": "03458537037",
                          "etternavn": "Testesen",
                          "fornavn": "Test",
                          "mellomnavn": null,
                          "startDato": "2026-09-21",
                          "sluttDato": null
                        }
                        """)));
        verify(postRequestedFor(urlPathEqualTo("/api/v1/dolly/avsluttRessurs"))
                .withRequestBody(equalToJson("""
                        {
                          "personident": "03458537037",
                          "etternavn": null,
                          "fornavn": null,
                          "mellomnavn": null,
                          "startDato": null,
                          "sluttDato": "2026-09-21"
                        }
                        """)));
    }

    @Test
    void shouldUseSkattekortEnvironmentAndNotTaxCardCleanupContract() {
        var taxCard = new SkattekortRequest(
                IDENT,
                new SkattekortData(
                        "2026-09-21",
                        2026,
                        "skattekortopplysningerOK",
                        List.of(new SkattekortData.Forskuddstrekk(
                                "loennFraNAV",
                                new SkattekortData.Frikort(50000))),
                        List.of()));
        var cleanup = new SkattekortRequest(
                IDENT,
                new SkattekortData(
                        "2026-09-21",
                        2026,
                        "ikkeSkattekort",
                        List.of(),
                        List.of()));
        stubFor(post(urlPathEqualTo("/skattekort/q2/api/v1/person/opprett"))
                .willReturn(ok()));
        stubFor(post(urlPathEqualTo(
                "/skattekort/q2/api/v1/person/hent-skattekort"))
                .willReturn(okJson("""
                        [{
                          "inntektsaar": 2026,
                          "resultatForSkattekort": "skattekortopplysningerOK",
                          "forskuddstrekkList": [{
                            "trekkode": "loennFraNAV",
                            "frikort": {"frikortBeloep": 50000}
                          }]
                        }]
                        """)));

        StepVerifier.create(new CreateSkattekortCommand(
                        webClient,
                        TOKEN,
                        FunctionalTestEnvironment.Q2,
                        taxCard,
                        TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetSkattekortCommand(
                        webClient,
                        TOKEN,
                        IDENT,
                        2026,
                        FunctionalTestEnvironment.Q2,
                        TIMEOUT).call())
                .assertNext(status -> assertThat(status.expectedTaxCardPresent()).isTrue())
                .verifyComplete();
        StepVerifier.create(new CreateSkattekortCommand(
                        webClient,
                        TOKEN,
                        FunctionalTestEnvironment.Q2,
                        cleanup,
                        TIMEOUT).call())
                .verifyComplete();

        verify(2, postRequestedFor(urlPathEqualTo(
                        "/skattekort/q2/api/v1/person/opprett"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
        verify(postRequestedFor(urlPathEqualTo(
                        "/skattekort/q2/api/v1/person/hent-skattekort"))
                .withRequestBody(equalToJson("""
                        {"fnr":"03458537037","inntektsaar":2026}
                        """)));
        verify(postRequestedFor(urlPathEqualTo(
                        "/skattekort/q2/api/v1/person/opprett"))
                .withRequestBody(equalToJson("""
                        {
                          "fnr": "03458537037",
                          "skattekort": {
                            "utstedtDato": "2026-09-21",
                            "inntektsaar": 2026,
                            "resultatForSkattekort": "ikkeSkattekort",
                            "forskuddstrekkList": [],
                            "tilleggsopplysningList": []
                          }
                        }
                        """)));
    }

    @Test
    void shouldTreatMissingReadResultsAsEmpty() {
        var arenaRequest = new ArenaRequest(List.of(new ArenaRequest.User(
                IDENT,
                "q1",
                LocalDate.of(2026, 9, 21),
                "IKVAL",
                true)));
        var timestamp = ZonedDateTime.of(
                2026, 9, 21, 10, 0, 0, 0, ZoneOffset.UTC);
        var krrRequest = new KrrRequest(
                IDENT,
                timestamp,
                false,
                true,
                "+4740000000",
                "dollystatus@example.invalid",
                "nb",
                timestamp,
                timestamp,
                timestamp,
                timestamp,
                timestamp,
                timestamp);
        var nomRequest = new NomRequest(
                IDENT,
                "Testesen",
                "Test",
                null,
                LocalDate.of(2026, 9, 21),
                null);
        stubFor(get(urlPathEqualTo(
                "/arena/q1/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .willReturn(aResponse().withStatus(204)));
        stubFor(get(urlPathEqualTo("/krrstub/api/v2/person/kontaktinformasjon"))
                .willReturn(aResponse().withStatus(404)));
        stubFor(post(urlPathEqualTo("/api/v1/dolly/hentRessurs"))
                .willReturn(aResponse().withStatus(404)));
        stubFor(post(urlPathEqualTo(
                "/skattekort/q1/api/v1/person/hent-skattekort"))
                .willReturn(okJson("[]")));

        StepVerifier.create(new GetArenaUserCommand(
                        webClient, TOKEN, IDENT, "q1", arenaRequest, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetKrrContactInformationCommand(
                        webClient, TOKEN, krrRequest, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetNomResourceCommand(
                        webClient, TOKEN, nomRequest, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetSkattekortCommand(
                        webClient,
                        TOKEN,
                        IDENT,
                        2026,
                        FunctionalTestEnvironment.Q1,
                        TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldEnforceRequestTimeout() {
        var request = new ArenaRequest(List.of(new ArenaRequest.User(
                IDENT,
                "q1",
                LocalDate.of(2026, 9, 21),
                "IKVAL",
                true)));
        stubFor(get(urlPathEqualTo(
                "/arena/q1/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .willReturn(okJson("{}").withFixedDelay(250)));

        StepVerifier.create(new GetArenaUserCommand(
                        webClient,
                        TOKEN,
                        IDENT,
                        "q1",
                        request,
                        Duration.ofMillis(50)).call())
                .expectError(java.util.concurrent.TimeoutException.class)
                .verify();
    }
}
