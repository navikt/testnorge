package no.nav.testnav.apps.statusfrontend.fagsystem;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SecondBatchTechnicalStatusProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.SecondBatchTechnicalStatusClient;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.Mockito.when;

@ExtendWith({
        MockitoExtension.class,
        DollyWireMockExtension.class
})
class SecondBatchTechnicalStatusContractTest {

    private static final String IDENT = "03458537037";
    private static final String TOKEN = "access-token";
    private static final RunId RUN_ID = RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");

    @Mock
    private TokenExchange tokenExchange;

    @Mock
    private Consumers consumers;

    private ServerProperties dollyProxy;
    private ServerProperties arbeidsplassenCvProxy;
    private ServerProperties organisasjonForvalter;
    private SecondBatchTechnicalStatusClient client;

    @BeforeEach
    void setUp() {
        var baseUrl = "http://localhost:" + DollyWireMockExtension.getPort();
        dollyProxy = ServerProperties.of("dev-fss", "dolly", "testnav-dolly-proxy", baseUrl);
        arbeidsplassenCvProxy = ServerProperties.of(
                "dev-gcp",
                "dolly",
                "testnav-arbeidsplassencv-proxy",
                baseUrl);
        organisasjonForvalter = ServerProperties.of(
                "dev-gcp",
                "dolly",
                "testnav-organisasjon-forvalter",
                baseUrl);
        var pdlProperties = new PdlFunctionalTestProperties();
        pdlProperties.setIdent(IDENT);
        when(consumers.getTestnavDollyProxy()).thenReturn(dollyProxy);
        when(consumers.getTestnavArbeidsplassenCVProxy()).thenReturn(arbeidsplassenCvProxy);
        when(consumers.getTestnavOrganisasjonForvalter()).thenReturn(organisasjonForvalter);
        client = new SecondBatchTechnicalStatusClient(
                tokenExchange,
                consumers,
                pdlProperties,
                new SecondBatchTechnicalStatusProperties(),
                WebClient.builder().build());
    }

    @Test
    void shouldCheckProxyReadinessWithoutMutation() {
        stubFor(get(urlPathEqualTo("/internal/health/readiness"))
                .willReturn(ok()));

        StepVerifier.create(client.checkArbeidsplassenCvProxy())
                .verifyComplete();
        StepVerifier.create(client.checkDollyProxy())
                .verifyComplete();
        StepVerifier.create(client.checkOrganisasjonForvalter())
                .verifyComplete();

        verify(3, getRequestedFor(urlPathEqualTo("/internal/health/readiness")));
    }

    @Test
    void shouldUseMedlReadContractAndAcceptMissingPerson() {
        when(tokenExchange.exchange(dollyProxy))
                .thenReturn(Mono.just(new AccessToken(TOKEN)));
        stubFor(get(urlPathEqualTo("/medl/rest/v1/person/" + IDENT))
                .willReturn(aResponse().withStatus(404)));

        StepVerifier.create(client.checkMedl())
                .verifyComplete();

        verify(getRequestedFor(urlPathEqualTo("/medl/rest/v1/person/" + IDENT))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
    }

    @Test
    void shouldUseFullmaktReadContractAndRequiredHeaders() {
        when(tokenExchange.exchange(dollyProxy))
                .thenReturn(Mono.just(new AccessToken(TOKEN)));
        stubFor(get(urlPathEqualTo("/fullmakt/api/fullmaktsgiver"))
                .willReturn(ok()));

        StepVerifier.create(client.checkFullmakt(RUN_ID))
                .verifyComplete();

        verify(getRequestedFor(urlPathEqualTo("/fullmakt/api/fullmaktsgiver"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Nav-Call-Id", equalTo(RUN_ID.value().toString()))
                .withHeader("Nav-Consumer-Id", equalTo("Dolly"))
                .withHeader("fnr", equalTo(IDENT)));
    }
}
