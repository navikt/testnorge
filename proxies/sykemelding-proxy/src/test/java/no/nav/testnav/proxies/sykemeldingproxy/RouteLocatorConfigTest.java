package no.nav.testnav.proxies.sykemeldingproxy;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@DollySpringBootTest()
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient(timeout = "PT30S")
class RouteLocatorConfigTest extends DollyApplicationContextTest {

    @MockitoBean
    AzureTrygdeetatenTokenService tokenService;

    @MockitoBean
    TokenExchange tokenExchange;

    @BeforeEach
    void setupMocks() {
        when(tokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-token")));
    }

    @Test
    void shouldRouteSyfosmreglerAndStripFirstPathSegment() {
        stubFor(get(urlEqualTo("/api/v1/validate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"valid\"}")));

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get()
                .uri("/syfosmregler/api/v1/validate")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("{\"result\":\"valid\"}");

        verify(1, getRequestedFor(urlEqualTo("/api/v1/validate")));
    }

    @Test
    void shouldRouteTsmAndStripPrefix() {
        stubFor(get(urlEqualTo("/api/v1/sykmelding"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"ok\"}")));

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get()
                .uri("/tsm/api/v1/sykmelding")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("{\"status\":\"ok\"}");

        verify(1, getRequestedFor(urlEqualTo("/api/v1/sykmelding")));
    }
}
