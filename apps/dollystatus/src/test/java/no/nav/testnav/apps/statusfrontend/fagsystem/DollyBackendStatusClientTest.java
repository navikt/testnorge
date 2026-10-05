package no.nav.testnav.apps.statusfrontend.fagsystem;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SecondBatchTechnicalStatusProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.mockito.Mockito.when;

@ExtendWith({
        MockitoExtension.class,
        DollyWireMockExtension.class
})
class DollyBackendStatusClientTest {

    @Mock
    private Consumers consumers;

    private DollyBackendStatusClient client;

    @BeforeEach
    void setUp() {
        var baseUrl = "http://localhost:" + DollyWireMockExtension.getPort();
        when(consumers.getTestnavDollyBackend())
                .thenReturn(ServerProperties.of("dev-gcp", "dolly", "dolly-backend", baseUrl));
        var properties = new SecondBatchTechnicalStatusProperties();
        properties.setRetryDelay(Duration.ofMillis(200));
        client = new DollyBackendStatusClient(
                consumers,
                properties,
                WebClient.builder().build());
    }

    @Test
    void shouldShareBackendStatusResponseBetweenTechnicalChecks() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(okJson("""
                        {
                          "Arbeidsregister (AAREG)": {
                            "testnav-dolly-proxy": {
                              "team": "dolly",
                              "alive": "OK",
                              "ready": "OK"
                            }
                          },
                          "Yrkesskade": {
                            "testnav-yrkesskade-proxy": {
                              "team": "dolly",
                              "alive": "OK",
                              "ready": "OK"
                            }
                          }
                        }
                        """)));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .verifyComplete();
        StepVerifier.create(client.check("Yrkesskade"))
                .verifyComplete();

        verify(1, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @Test
    void shouldRejectMissingAndEmptyStatusesWithoutRetry() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(okJson("""
                        {
                          "Tom": {}
                        }
                        """)));

        StepVerifier.create(client.check("Mangler"))
                .expectErrorMessage("Teknisk status mangler.")
                .verify();
        StepVerifier.create(client.check("Tom"))
                .expectErrorMessage("Teknisk status mangler.")
                .verify();

        verify(1, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @Test
    void shouldFetchAgainWhenStatusIsNotOkAndAcceptRecovery() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .inScenario("status")
                .whenScenarioStateIs(STARTED)
                .willReturn(okJson(statusResponse("DOWN")))
                .willSetStateTo("recovered"));
        stubFor(get(urlPathEqualTo("/internal/status"))
                .inScenario("status")
                .whenScenarioStateIs("recovered")
                .willReturn(okJson(statusResponse("OK"))));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .verifyComplete();

        verify(2, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @Test
    void shouldRejectStatusThatStaysNotOkAfterRetries() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(okJson(statusResponse("DOWN"))));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .expectErrorMessage("Teknisk status er ikke OK.")
                .verify();

        verify(3, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @Test
    void shouldRetryTransientBackendFailure() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .inScenario("status")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("recovered"));
        stubFor(get(urlPathEqualTo("/internal/status"))
                .inScenario("status")
                .whenScenarioStateIs("recovered")
                .willReturn(okJson(statusResponse("OK"))));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .verifyComplete();

        verify(2, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @Test
    void shouldNotShareFailedBackendResponseBetweenTechnicalChecks() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(aResponse().withStatus(503)));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .expectError(WebClientResponseException.ServiceUnavailable.class)
                .verify();
        StepVerifier.create(client.check("Yrkesskade"))
                .expectError(WebClientResponseException.ServiceUnavailable.class)
                .verify();

        verify(6, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 204})
    void shouldRejectEmptyBackendResponseWithoutSharingIt(int statusCode) {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(aResponse().withStatus(statusCode)));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .expectErrorMessage("Teknisk status mangler.")
                .verify();
        StepVerifier.create(client.check("Yrkesskade"))
                .expectErrorMessage("Teknisk status mangler.")
                .verify();

        verify(2, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    private static String statusResponse(String ready) {
        return """
                {
                  "Arbeidsregister (AAREG)": {
                    "testnav-dolly-proxy": {
                      "team": "dolly",
                      "alive": "OK",
                      "ready": "%s"
                    }
                  }
                }
                """.formatted(ready);
    }
}
