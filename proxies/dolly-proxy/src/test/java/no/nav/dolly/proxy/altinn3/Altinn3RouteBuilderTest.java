package no.nav.dolly.proxy.altinn3;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasConsumer;
import no.nav.dolly.libs.texas.TexasToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@Import(MockTexasConfig.class)
class Altinn3RouteBuilderTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private com.github.tomakehurst.wiremock.WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlMatching("/altinn3/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Altinn3 Service Response")));
    }

    @Test
    @Disabled("For now")
    void shouldRouteAndAddAuthHeaderForAltinn3Paths() {
        webClient
                .get()
                .uri("/altinn3/some/path")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Altinn3 Service Response");
        wireMockServer.verify(getRequestedFor(urlEqualTo("/altinn3/some/path"))
                .withHeader("Authorization", equalTo("Bearer " + MockTexasConfig.MOCKED_TOKEN_VALUE)));
    }

    @Test
    void shouldNotRouteForInternalPaths() {
        webClient
                .get()
                .uri("/internal/health")
                .exchange()
                .expectStatus()
                .isOk();
        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/internal/health")));
    }
}