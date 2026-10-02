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
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
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
        client = new DollyBackendStatusClient(
                consumers,
                new SecondBatchTechnicalStatusProperties(),
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
    void shouldRejectMissingEmptyAndDownStatuses() {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(okJson("""
                        {
                          "Tom": {},
                          "Nede": {
                            "testnav-dolly-proxy": {
                              "team": "dolly",
                              "alive": "OK",
                              "ready": "DOWN"
                            }
                          }
                        }
                        """)));

        StepVerifier.create(client.check("Mangler"))
                .expectError(IllegalStateException.class)
                .verify();
        StepVerifier.create(client.check("Tom"))
                .expectError(IllegalStateException.class)
                .verify();
        StepVerifier.create(client.check("Nede"))
                .expectError(IllegalStateException.class)
                .verify();

        verify(1, getRequestedFor(urlPathEqualTo("/internal/status")));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 204})
    void shouldRejectAndShareEmptyBackendResponseAsError(int statusCode) {
        stubFor(get(urlPathEqualTo("/internal/status"))
                .willReturn(aResponse().withStatus(statusCode)));

        StepVerifier.create(client.check("Arbeidsregister (AAREG)"))
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && error.getMessage().equals("Teknisk status mangler."))
                .verify();
        StepVerifier.create(client.check("Yrkesskade"))
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && error.getMessage().equals("Teknisk status mangler."))
                .verify();

        verify(1, getRequestedFor(urlPathEqualTo("/internal/status")));
    }
}
