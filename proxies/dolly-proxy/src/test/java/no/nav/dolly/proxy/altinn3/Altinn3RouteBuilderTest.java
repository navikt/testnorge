package no.nav.dolly.proxy.altinn3;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasConsumer;
import no.nav.dolly.libs.texas.TexasToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
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
class Altinn3RouteBuilderTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private com.github.tomakehurst.wiremock.WireMockServer wireMockServer;

    private static final String MOCKED_TOKEN_VALUE = "mocked-altinn3-token";
    private static final String ALTINN3_CONSUMER_NAME = "testnav-altinn3-tilgang-service-prod";

    /**
     * Test config to provide a mocked Texas bean before the RouteLocatorBuilder runs.
     */
    @TestConfiguration
    @EnableWebFluxSecurity
    static class MockTexasConfig {

        @Bean
        @Primary
        public Texas texas(@Value("${wiremock.server.port}") String wiremockPort) {

            var mockTexas = Mockito.mock(Texas.class);

            when(mockTexas.get(ALTINN3_CONSUMER_NAME))
                    .thenReturn(Mono.just(new TexasToken(MOCKED_TOKEN_VALUE, "3600", "test-token")));

            var mockConsumer = new TexasConsumer();
            mockConsumer.setName(ALTINN3_CONSUMER_NAME);
            mockConsumer.setUrl("http://localhost:" + wiremockPort);
            when(mockTexas.consumer(ALTINN3_CONSUMER_NAME))
                    .thenReturn(Optional.of(mockConsumer));

            return mockTexas;

        }

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                    .build();
        }

    }

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlMatching("/altinn3/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Altinn3 Service Response")));
    }

    @Test
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
                .withHeader("Authorization", equalTo("Bearer " + MOCKED_TOKEN_VALUE)));
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