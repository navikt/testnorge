package no.nav.testnav.proxies.sykemeldingproxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@Import(TestSecurityConfig.class)
class RouteLocatorConfigTest extends DollyApplicationContextTest {

    private static WireMockServer wireMockServer;

    @MockitoBean
    AzureTrygdeetatenTokenService tokenService;

    @MockitoBean
    TokenExchange tokenExchange;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", () -> wireMockServer.port());
        registry.add("consumers.syfosmregler.url", () -> "http://localhost:" + wireMockServer.port());
        registry.add("consumers.tsm.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeEach
    void setupMocks() {
        when(tokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-token")));
        wireMockServer.resetAll();
    }

    @Test
    void testWebTestClientNotNull() {
        assert webTestClient != null : "webTestClient should not be null";
    }

    @Test
    void shouldRouteSyfosmreglerAndStripFirstPathSegment() {
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/validate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"valid\"}")));

        webTestClient
                .get()
                .uri("/syfosmregler/api/v1/validate")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("{\"result\":\"valid\"}");

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/api/v1/validate")));
    }

    @Test
    void shouldRouteTsmAndStripPrefix() {
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/sykmelding"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"ok\"}")));

        webTestClient
                .get()
                .uri("/tsm/api/v1/sykmelding")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("{\"status\":\"ok\"}");

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/api/v1/sykmelding")));
    }
}

