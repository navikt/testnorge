package no.nav.testnav.proxies.aareg;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class RouteLocatorConfigTest {

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
    @ValueSource(strings = {
            "q1"/*, "q2", "q4", "q5",
            "t0", "t1", "t3", "t4", "t5"*/}
    )
    @Disabled // TODO: Fix 401 on TrygdeetatenAzureAdTokenService (presumably).
    void getOnUrlWithEnvironmentShouldRedirectToNewUrl(String env) {

        final var FROM_ACTUAL_URI = "/" + env + "/api/v1/arbeidsforhold/";
        final var TO_EXPECTED_URI = "/aareg-services/api/v1/arbeidsforhold/";
        final var BODY = "I have been correctly routed from " + FROM_ACTUAL_URI + " to " + TO_EXPECTED_URI;

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

}
