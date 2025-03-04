package no.nav.testnav.proxies.skjermingsregisterproxy;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@DollySpringBootTest(
        properties = "consumers.skjermingsregister.url=http://localhost:${wiremock.server.port}"
)
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient(timeout = "PT1M")
class RouteLocatorConfigTest extends DollyApplicationContextTest {

    @TestConfiguration
    static class TestAuthenticationConfig {

        @Primary
        @Bean
        GatewayFilter getNoopAuthenticationFilter() {
            return (exchange, chain) -> chain.filter(exchange);

        }

    }

    @Test
    void shouldRouteToStub() {

        stubFor(
                get(urlEqualTo("/testing/route"))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                                        .withBody("Some content")
                        )
        );

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri("/testing/route")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_PLAIN_VALUE)
                .expectBody(String.class).isEqualTo("Some content");

    }

}
