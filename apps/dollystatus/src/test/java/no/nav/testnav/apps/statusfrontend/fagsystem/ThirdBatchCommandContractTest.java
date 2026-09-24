package no.nav.testnav.apps.statusfrontend.fagsystem;

import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.CreateBrregstubRoleOverviewCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.DeleteBrregstubOrganizationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.DeleteBrregstubRoleOverviewCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.GetBrregstubOrganizationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.GetBrregstubRoleOverviewCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command.CreateInntektstubIncomeCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command.DeleteInntektstubIncomeCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command.GetInntektstubIncomeCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.sigrun.command.GetSigrunReadinessCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command.CreateSkjermingsregisterCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command.GetSkjermingsregisterCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command.UpdateSkjermingsregisterCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.command.CreateUdiPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.command.DeleteUdiPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.command.GetUdiPersonCommand;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DollyWireMockExtension.class)
class ThirdBatchCommandContractTest {

    private static final String IDENT = "03458537037";
    private static final String TOKEN = "access-token";
    private static final int ORGANIZATION_NUMBER = 991825827;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:" + DollyWireMockExtension.getPort())
            .build();

    @Test
    void shouldUseBrregstubContractsAndAfterCheckBothDeletes() {
        var request = brregstubRequest();
        var response = """
                {
                  "fnr": "03458537037",
                  "fodselsdato": "1996-09-21",
                  "navn": {"navn1": "Dollystatus", "navn3": "Testperson"},
                  "adresse": {
                    "adresse1": "Testveien 1",
                    "postnr": "0555",
                    "poststed": "Oslo",
                    "landKode": "NO",
                    "kommunenr": "0301"
                  },
                  "enheter": [{
                    "registreringsdato": "2026-09-21",
                    "rolle": "DELT",
                    "rollebeskrivelse": "Deltakere",
                    "orgNr": 991825827,
                    "foretaksNavn": {"navn1": "Dollystatus testorganisasjon"},
                    "forretningsAdresse": {
                      "adresse1": "Testveien 1",
                      "postnr": "0555",
                      "poststed": "Oslo",
                      "landKode": "NO",
                      "kommunenr": "0301"
                    },
                    "postAdresse": {
                      "adresse1": "Testveien 1",
                      "postnr": "0555",
                      "poststed": "Oslo",
                      "landKode": "NO",
                      "kommunenr": "0301"
                    },
                    "personRolle": [{"egenskap": "DELTAGER", "fratraadt": false}]
                  }],
                  "hovedstatus": 0,
                  "understatuser": []
                }
                """;
        stubFor(post(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        stubFor(get(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .willReturn(okJson(response)));
        stubFor(get(urlPathEqualTo(
                "/brregstub/api/v1/hentrolle/" + ORGANIZATION_NUMBER))
                .willReturn(okJson("""
                        {
                          "orgnr": 991825827,
                          "registreringsdato": "2026-09-21",
                          "deltakere": {
                            "roller": [{"fodselsnr": "03458537037"}]
                          }
                        }
                        """)));
        stubFor(delete(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .willReturn(ok()));
        stubFor(delete(urlPathEqualTo(
                "/brregstub/api/v1/hentrolle/" + ORGANIZATION_NUMBER))
                .willReturn(ok()));

        StepVerifier.create(new CreateBrregstubRoleOverviewCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetBrregstubRoleOverviewCommand(
                        webClient, TOKEN, IDENT, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.expectedDataPresent()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetBrregstubOrganizationCommand(
                        webClient,
                        TOKEN,
                        ORGANIZATION_NUMBER,
                        IDENT,
                        LocalDate.of(2026, 9, 21),
                        TIMEOUT).call())
                .assertNext(status -> assertThat(status.expectedDataPresent()).isTrue())
                .verifyComplete();
        StepVerifier.create(new DeleteBrregstubRoleOverviewCommand(
                        webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new DeleteBrregstubOrganizationCommand(
                        webClient, TOKEN, ORGANIZATION_NUMBER, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson(response, true, true)));
        verify(getRequestedFor(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Personident", equalTo(IDENT)));
        verify(deleteRequestedFor(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .withHeader("Nav-Personident", equalTo(IDENT)));
        verify(deleteRequestedFor(urlPathEqualTo(
                "/brregstub/api/v1/hentrolle/" + ORGANIZATION_NUMBER)));

        stubFor(get(urlPathEqualTo("/brregstub/api/v2/rolleoversikt"))
                .willReturn(aResponse().withStatus(404)));
        stubFor(get(urlPathEqualTo(
                "/brregstub/api/v1/hentrolle/" + ORGANIZATION_NUMBER))
                .willReturn(aResponse().withStatus(404)));
        StepVerifier.create(new GetBrregstubRoleOverviewCommand(
                        webClient, TOKEN, IDENT, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
        StepVerifier.create(new GetBrregstubOrganizationCommand(
                        webClient,
                        TOKEN,
                        ORGANIZATION_NUMBER,
                        IDENT,
                        LocalDate.of(2026, 9, 21),
                        TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldUseInntektstubReservedDataAndBroadDeleteContract() {
        var request = new InntektstubRequest(
                IDENT,
                "2099-12",
                "991825827",
                "991825827",
                List.of(new InntektstubRequest.Income(
                        "LOENNSINNTEKT",
                        1234.0,
                        "fastloenn",
                        "kontantytelse",
                        true,
                        true)));
        var response = """
                [{
                  "norskIdent": "03458537037",
                  "aarMaaned": "2099-12",
                  "opplysningspliktig": "991825827",
                  "virksomhet": "991825827",
                  "inntektsliste": [{
                    "inntektstype": "LOENNSINNTEKT",
                    "beloep": 1234.0,
                    "beskrivelse": "fastloenn",
                    "fordel": "kontantytelse",
                    "inngaarIGrunnlagForTrekk": true,
                    "utloeserArbeidsgiveravgift": true
                  }]
                }]
                """;
        stubFor(post(urlPathEqualTo("/inntektstub/api/v2/inntektsinformasjon"))
                .willReturn(okJson(response)));
        stubFor(get(urlPathEqualTo("/inntektstub/api/v2/inntektsinformasjon"))
                .willReturn(okJson(response)));
        stubFor(delete(urlPathEqualTo("/inntektstub/api/v2/personer"))
                .willReturn(ok()));

        StepVerifier.create(new CreateInntektstubIncomeCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetInntektstubIncomeCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.expectedDataPresent()).isTrue())
                .verifyComplete();
        StepVerifier.create(new DeleteInntektstubIncomeCommand(
                        webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo(
                "/inntektstub/api/v2/inntektsinformasjon"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson(response)));
        verify(getRequestedFor(urlPathEqualTo(
                "/inntektstub/api/v2/inntektsinformasjon"))
                .withQueryParam("norske-identer", equalTo(IDENT))
                .withQueryParam("historikk", equalTo("true")));
        verify(deleteRequestedFor(urlPathEqualTo("/inntektstub/api/v2/personer"))
                .withQueryParam("norske-identer", equalTo(IDENT)));

        stubFor(get(urlPathEqualTo("/inntektstub/api/v2/inntektsinformasjon"))
                .willReturn(okJson("[]")));
        StepVerifier.create(new GetInntektstubIncomeCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldRejectInntektstubValidationErrorsReturnedWithHttpOk(boolean nestedError) {
        var request = new InntektstubRequest(
                IDENT, "2099-12", "991825827", "991825827",
                List.of(new InntektstubRequest.Income(
                        "LOENNSINNTEKT", 1234.0, "fastloenn", "kontantytelse", true, true)));
        var errorField = "\"feilmelding\": \"Sensitive downstream validation details\",";
        stubFor(post(urlPathEqualTo("/inntektstub/api/v2/inntektsinformasjon"))
                .willReturn(okJson("""
                        [{
                          %s
                          "norskIdent": "03458537037",
                          "aarMaaned": "2099-12",
                          "opplysningspliktig": "991825827",
                          "virksomhet": "991825827",
                          "inntektsliste": [{
                            %s
                            "inntektstype": "LOENNSINNTEKT",
                            "beloep": 1234.0,
                            "beskrivelse": "fastloenn",
                            "fordel": "kontantytelse",
                            "inngaarIGrunnlagForTrekk": true,
                            "utloeserArbeidsgiveravgift": true
                          }]
                        }]
                        """.formatted(nestedError ? "" : errorField, nestedError ? errorField : ""))));

        StepVerifier.create(new CreateInntektstubIncomeCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Inntektstub avviste testdata.")
                        .hasNoCause())
                .verify();
    }

    @Test
    void shouldUseSkjermingsregisterContractsAndReturnTerminatedState() {
        var activeRequest = new SkjermingsregisterRequest(
                "Testperson",
                "Dollystatus",
                IDENT,
                LocalDateTime.parse("2026-09-21T10:00:00"),
                LocalDateTime.parse("2026-09-22T10:00:00"));
        var terminatedRequest = new SkjermingsregisterRequest(
                "Testperson",
                "Dollystatus",
                IDENT,
                LocalDateTime.parse("2026-09-21T10:00:00"),
                LocalDateTime.parse("2026-09-21T10:00:00"));
        var activeResponse = """
                {
                  "etternavn": "Testperson",
                  "fornavn": "Dollystatus",
                  "personident": "03458537037",
                  "skjermetFra": "2026-09-21T10:00:00",
                  "skjermetTil": "2026-09-22T10:00:00"
                }
                """;
        var terminatedResponse = """
                {
                  "etternavn": "Testperson",
                  "fornavn": "Dollystatus",
                  "personident": "03458537037",
                  "skjermetFra": "2026-09-21T10:00:00",
                  "skjermetTil": "2026-09-21T10:00:00"
                }
                """;
        stubFor(post(urlPathEqualTo("/skjermingsregister/api/v1/skjerming/dolly"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(activeResponse)));
        stubFor(get(urlPathEqualTo("/skjermingsregister/api/v1/skjerming/dolly"))
                .willReturn(okJson(activeResponse)));
        stubFor(put(urlPathEqualTo("/skjermingsregister/api/v1/skjerming/dolly"))
                .willReturn(ok()));

        StepVerifier.create(new CreateSkjermingsregisterCommand(
                        webClient, TOKEN, activeRequest, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetSkjermingsregisterCommand(
                        webClient,
                        TOKEN,
                        activeRequest,
                        LocalDateTime.parse("2026-09-21T10:00:00"),
                        TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.active()).isTrue();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();
        StepVerifier.create(new UpdateSkjermingsregisterCommand(
                        webClient, TOKEN, terminatedRequest, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo(
                "/skjermingsregister/api/v1/skjerming/dolly"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson(activeResponse)));
        verify(getRequestedFor(urlPathEqualTo(
                "/skjermingsregister/api/v1/skjerming/dolly"))
                .withHeader("personident", equalTo(IDENT)));
        verify(putRequestedFor(urlPathEqualTo(
                "/skjermingsregister/api/v1/skjerming/dolly"))
                .withRequestBody(equalToJson(terminatedResponse)));

        stubFor(get(urlPathEqualTo("/skjermingsregister/api/v1/skjerming/dolly"))
                .willReturn(okJson(terminatedResponse)));
        StepVerifier.create(new GetSkjermingsregisterCommand(
                        webClient,
                        TOKEN,
                        terminatedRequest,
                        LocalDateTime.parse("2026-09-21T10:00:00"),
                        TIMEOUT).call())
                .assertNext(status -> {
                    assertThat(status.empty()).isFalse();
                    assertThat(status.terminated()).isTrue();
                    assertThat(status.expectedDataPresent()).isTrue();
                })
                .verifyComplete();

        stubFor(get(urlPathEqualTo("/skjermingsregister/api/v1/skjerming/dolly"))
                .willReturn(aResponse().withStatus(404)));
        StepVerifier.create(new GetSkjermingsregisterCommand(
                        webClient,
                        TOKEN,
                        activeRequest,
                        LocalDateTime.parse("2026-09-21T10:00:00"),
                        TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldUseUdiContractsAndAfterCheckDelete() {
        var request = new UdiRequest(
                IDENT,
                new UdiRequest.Name("Dollystatus", null, "Testperson"),
                LocalDate.of(1996, 9, 21),
                false,
                true,
                false,
                "NEI",
                LocalDate.of(2026, 9, 21));
        var requestBody = """
                {
                  "ident": "03458537037",
                  "navn": {"fornavn": "Dollystatus", "etternavn": "Testperson"},
                  "foedselsDato": "1996-09-21",
                  "avgjoerelseUavklart": false,
                  "harOppholdsTillatelse": true,
                  "flyktning": false,
                  "soeknadOmBeskyttelseUnderBehandling": "NEI",
                  "soknadDato": "2026-09-21"
                }
                """;
        var response = "{\"person\":" + requestBody + "}";
        stubFor(post(urlPathEqualTo("/udistub/api/v1/person"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        stubFor(get(urlPathEqualTo("/udistub/api/v1/person/" + IDENT))
                .willReturn(okJson(response)));
        stubFor(delete(urlPathEqualTo("/udistub/api/v1/person"))
                .willReturn(ok()));

        StepVerifier.create(new CreateUdiPersonCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .verifyComplete();
        StepVerifier.create(new GetUdiPersonCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.expectedDataPresent()).isTrue())
                .verifyComplete();
        StepVerifier.create(new DeleteUdiPersonCommand(
                        webClient, TOKEN, IDENT, TIMEOUT).call())
                .verifyComplete();

        verify(postRequestedFor(urlPathEqualTo("/udistub/api/v1/person"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withRequestBody(equalToJson(requestBody, true, true)));
        verify(getRequestedFor(urlPathEqualTo("/udistub/api/v1/person/" + IDENT))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
        verify(deleteRequestedFor(urlPathEqualTo("/udistub/api/v1/person"))
                .withHeader("Nav-Personident", equalTo(IDENT)));

        stubFor(get(urlPathEqualTo("/udistub/api/v1/person/" + IDENT))
                .willReturn(aResponse().withStatus(404)));
        StepVerifier.create(new GetUdiPersonCommand(
                        webClient, TOKEN, request, TIMEOUT).call())
                .assertNext(status -> assertThat(status.empty()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldUseReadOnlySigrunTechnicalContract() {
        stubFor(get(urlEqualTo("/sigrunstub/internal/health/readiness"))
                .willReturn(ok()));

        StepVerifier.create(new GetSigrunReadinessCommand(
                        webClient, TOKEN, TIMEOUT).call())
                .verifyComplete();

        verify(getRequestedFor(urlEqualTo("/sigrunstub/internal/health/readiness"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
    }

    private static BrregstubRequest brregstubRequest() {
        var name = new BrregstubRequest.Name("Dollystatus", null, "Testperson");
        var address = new BrregstubRequest.Address(
                "Testveien 1",
                null,
                null,
                "0555",
                "Oslo",
                "NO",
                "0301");
        return new BrregstubRequest(
                IDENT,
                LocalDate.of(1996, 9, 21),
                name,
                address,
                List.of(new BrregstubRequest.Role(
                        LocalDate.of(2026, 9, 21),
                        "DELT",
                        "Deltakere",
                        ORGANIZATION_NUMBER,
                        new BrregstubRequest.Name(
                                "Dollystatus testorganisasjon",
                                null,
                                null),
                        address,
                        address,
                        List.of(new BrregstubRequest.RoleStatus("DELTAGER", false)))),
                0,
                List.of());
    }
}
