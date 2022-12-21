package no.nav.testnav.proxies.aareg;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class RouteLocatorConfigTest {

    @Autowired
    private WebTestClient client;

    @ParameterizedTest
    @ValueSource(strings = {
            "q1"/*, "q2", "q4", "q5",
            "t0", "t1", "t3", "t4", "t5"*/}
    )
    @Disabled("Løs problem med TrygdeetatenAzureAdTokenService")
    void getOnUrlWithEnvironmentShouldRedirectToNewUrl(String env) {

        final var FROM_ACTUAL_URI = "/" + env + "/api/v1/arbeidsforhold/";
        final var TO_EXPECTED_URI = "/aareg-services/api/v1/arbeidsforhold/";
        final var BODY = "I have been correctly routed from " + FROM_ACTUAL_URI + " to " + TO_EXPECTED_URI;

        stubFor(
                get(urlEqualTo(TO_EXPECTED_URI))
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
