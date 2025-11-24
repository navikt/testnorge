package no.nav.dolly.proxy.texas;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class RouteLocatorTest extends DollyApplicationContextTest {

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

    @MockitoBean
    @SuppressWarnings("unused")
    private Texas texas;

    @BeforeEach
    void beforeEach() {
        when(texas.get(anyString()))
                .thenReturn(Mono.just(new TexasToken("", "", "")));
        wiremock.stubFor(post(urlEqualTo("/api/v1/token"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(GET_TOKEN_BODY))
                .willReturn(aResponse().withStatus(200)));
        wiremock.stubFor(post(urlEqualTo("/api/v1/token/exchange"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(EXCHANGE_TOKEN_BODY))
                .willReturn(aResponse().withStatus(200)));
        wiremock.stubFor(post(urlEqualTo("/api/v1/introspect"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
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
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Dolly TEST")
                .bodyValue(GET_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .isOk();

        // No custom auth header.
        client
                .post()
                .uri("/api/v1/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(GET_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .is4xxClientError();

        client
                .post()
                .uri("/api/v1/token/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Dolly TEST")
                .bodyValue(EXCHANGE_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .isOk();

        client
                .post()
                .uri("/api/v1/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Dolly TEST")
                .bodyValue(INTROSPECT_TOKEN_BODY)
                .exchange()
                .expectStatus()
                .isOk();
    }

}