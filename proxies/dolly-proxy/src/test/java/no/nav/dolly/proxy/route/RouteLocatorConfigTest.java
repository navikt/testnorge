package no.nav.dolly.proxy.route;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@DollySpringBootTest(properties = {
        "management.endpoints.web.exposure.include=health",
        "management.endpoints.web.base-path=/internal"
})
@AutoConfigureWebTestClient
class RouteLocatorConfigTest {

    @Autowired
    private WebTestClient webClient;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("consumers.inntektsstub.url", () -> wireMockServer.baseUrl());
    }

    @Disabled
    @Test
    void testInntektsstub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked inntektsstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/inntektsstub-proxy" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

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