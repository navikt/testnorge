package no.nav.dolly.proxy.texas;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class ApplicationContextTest extends DollyApplicationContextTest {

    private static final String GET_TOKEN_BODY = """
            {
                "identity_provider": "azuread",
                "target": "api://dev-gcp.dolly.some-app-name/.default"
            }
            """;
    private static final String EXCHANGE_TOKEN_BODY = """
            {
                "identity_provider": "azuread",
                "target": "api://dev-gcp.dolly.some-app-name/.default",
                "user_token": "TOKENVALUE"
            }
            """;
    private static final String INTROSPECT_TOKEN_BODY = """
            {
                "identity_provider": "azuread",
                "token": "TOKENVALUE"
            }
            """;

    @Autowired
    private WebTestClient client;

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WireMockServer wiremock;

    @BeforeEach
    void beforeEach() {
        wiremock.stubFor(post(urlEqualTo("/api/v1/token"))
                .withRequestBody(equalToJson(GET_TOKEN_BODY))
                .willReturn(aResponse().withStatus(200)));
        wiremock.stubFor(post(urlEqualTo("/api/v1/token/exchange"))
                .withRequestBody(equalToJson(EXCHANGE_TOKEN_BODY))
                .willReturn(aResponse().withStatus(200)));
        wiremock.stubFor(post(urlEqualTo("/api/v1/introspect"))
                .withRequestBody(equalToJson(INTROSPECT_TOKEN_BODY))
                .willReturn(aResponse().withStatus(200)));
    }

    @Test
    void testRoutes() {
        assertThat(routeLocator.getRoutes().count().block())
                .isEqualTo(3);

        client
                .post()
                .uri("/api/v1/token")
                .bodyValue(GET_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .isOk();

        client
                .post()
                .uri("/api/v1/token/exchange")
                .bodyValue(EXCHANGE_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .isOk();

        client
                .post()
                .uri("/api/v1/introspect")
                .bodyValue(INTROSPECT_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .isOk();
    }

}
