package no.nav.testnav.proxies.skjermingsregisterproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.skjermingsregister.url=http://localhost:${wiremock.server.port}"
)
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient
class RouteLocatorConfigTest {

    @Autowired
    private WebTestClient webClient;

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

        webClient
                .mutateWith(mockOAuth2Login())
                .get().uri("/testing/route")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_PLAIN_VALUE)
                .expectBody(String.class).isEqualTo("Some content");

    }

    @TestConfiguration
    static class TestAuthenticationConfig {

        @Primary
        @Bean
        GatewayFilter getNoopAuthenticationFilter() {
            return (exchange, chain) -> chain.filter(exchange);

        }

    }

}
