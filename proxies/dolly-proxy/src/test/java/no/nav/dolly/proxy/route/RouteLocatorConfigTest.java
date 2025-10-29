package no.nav.dolly.proxy.route;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.proxy.auth.FakedingsCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DollySpringBootTest(properties = {
        "management.endpoints.web.exposure.include=health",
        "management.endpoints.web.base-path=/internal"
})
@AutoConfigureWebTestClient
class RouteLocatorConfigTest {

    @MockitoBean
    private TokenExchange tokenExchange;

    @MockitoSpyBean
    private AzureNavTokenService navTokenService;

    @MockitoSpyBean
    private AzureTrygdeetatenTokenService trygdeetatenTokenService;

    @MockitoBean
    private TokenXService tokenXService;

    @Autowired
    private WebTestClient webClient;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("app.targets.fullmakt", () -> wireMockServer.baseUrl());
        registry.add("app.targets.histark", () -> wireMockServer.baseUrl());
        registry.add("app.targets.inntektstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.skjermingsregister", () -> wireMockServer.baseUrl());
        registry.add("app.targets.sigrunstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.udistub", () -> wireMockServer.baseUrl());
    }

    @BeforeEach
    public void setup() {
        webClient = webClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
        when(tokenExchange.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-tokenx-token")));
        when(navTokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-nav-token")));
        when(trygdeetatenTokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-trygdeetaten-token")));
        when(tokenXService.exchange(any(), any()))
                .thenReturn(Mono.just(new AccessToken("dummy-tokenx-token")));
    }

    @Test
    void testFullmakt() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked fullmakt";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/fullmakt" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json; charset=UTF-8")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-tokenx-token")));

    }

    @Test
    void testHistark() {

        var downstreamPath = "/api/saksmapper/1";
        var responseBody = "Success from mocked histark";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/histark" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testInntektstub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked inntektstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/inntektstub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testSigrunstub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked sigrunstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/sigrunstub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testSkjermingsregister() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked skjermingsregister";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/skjermingsregister" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

    }

    @Test
    void testUdistub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked udistub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/udistub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json; charset=UTF-8")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-nav-token")));

    }

    @Test
    void testInternalEndpoints() {

        webClient
                .get()
                .uri("/internal/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/vnd.spring-boot.actuator.v3+json")
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");

        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/internal/health")));

    }
}