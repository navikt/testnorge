package no.nav.testnav.proxies.aaregproxy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "app.modapp.uri.pattern=http://localhost:${wiremock.server.port}",
        "sts.preprod.token.provider.url=http://localhost:${wiremock.server.port}/rest/v1/sts/token",
        "sts.test.token.provider.url=http://localhost:${wiremock.server.port}/rest/v1/sts/token"}
)
@AutoConfigureWireMock(port = 0)
class AaregProxyApplicationStarterTest {

    private static final Random RANDOM = new Random();

    @Autowired
    private WebTestClient client;

    @BeforeAll
    public static void createStubForSts() {
        stubFor(
            get(urlEqualTo("/rest/v1/sts/token?grant_type=client_credentials&scope=openid"))
                .willReturn(aResponse()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody("""
                        {
                            "access_token": "token",
                            "expires_in": 123
                        }
                        """))
        );
    }

    @ParameterizedTest
    @ValueSource(strings =
        {
            "q1", "q2", "q4", "q5", "qx",
            "t0", "t1", "t2", "t3", "t4", "t5", "t13"
        }

    )
    void getOnUrlWithQueryParamShouldBeRoutedToNewURLWithHeader(String env) {

        final var ID = RANDOM.nextInt(10000, 99999);
        final var FROM_ACTUAL_URI = "/api/v1/arbeidsforhold/" + ID + "?miljoe=" + env;
        final var TO_EXPECTED_URI = "/aareg-services/api/v1/arbeidsforhold/" + ID;
        final var BODY = "I have been correctly routed from " + FROM_ACTUAL_URI + " to " + TO_EXPECTED_URI + " with header miljoe=" + env;

        stubFor(
            get(urlEqualTo(TO_EXPECTED_URI))
                .withHeader("miljoe", equalTo(env))
                .willReturn(aResponse()
                    .withBody(BODY))
        );

        client
            .get().uri(FROM_ACTUAL_URI)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(BODY);

    }

    @ParameterizedTest
    @ValueSource(strings =
        {
            "q1", "q2", "q4", "q5", "qx",
            "t0", "t1", "t2", "t3", "t4", "t5", "t13"
        }

    )
    void getOnUrlWithPathParamShouldBeRoutedToNewURLWithHeader(String env) {

        final var ID = RANDOM.nextInt(10000, 99999);
        final var FROM_ACTUAL_URI = "/api/v1/arbeidsforhold/" + ID + "/miljoe/" + env;
        final var TO_EXPECTED_URI = "/aareg-services/api/v1/arbeidsforhold/" + ID;
        final var BODY = "I have been correctly routed from " + FROM_ACTUAL_URI + " to " + TO_EXPECTED_URI + " with header miljoe=" + env;

        stubFor(
            get(urlEqualTo(TO_EXPECTED_URI))
                .withHeader("miljoe", equalTo(env))
                .willReturn(aResponse()
                    .withBody(BODY))
        );

        client
            .get().uri(FROM_ACTUAL_URI)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(BODY);

    }

    @TestConfiguration
    static class Config {

        @Primary
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                .authorizeExchange()
                .pathMatchers("/api/v1/**")
                .permitAll()
                .and().build();
        }

    }

}