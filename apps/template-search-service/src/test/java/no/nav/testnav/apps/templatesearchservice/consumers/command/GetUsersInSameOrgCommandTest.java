package no.nav.testnav.apps.templatesearchservice.consumers.command;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.nav.testnav.apps.templatesearchservice.exception.BrukerServiceUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class GetUsersInSameOrgCommandTest {

    private WireMockServer wireMockServer;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        webClient = WebClient.builder()
                .baseUrl(wireMockServer.baseUrl())
                .build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldGetOrganizationUsersWithBearerToken() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/tilgang"))
                .withQueryParam("brukerId", equalTo("hashed-id"))
                .withHeader(AUTHORIZATION, equalTo("Bearer exchanged-token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody("""
                                {"brukere":["hashed-id","colleague-id"]}
                                """)));

        StepVerifier.create(new GetUsersInSameOrgCommand(
                        webClient,
                        "hashed-id",
                        "exchanged-token").call())
                .assertNext(response -> assertThat(response.brukere())
                        .containsExactly("hashed-id", "colleague-id"))
                .verifyComplete();

        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/api/v1/tilgang")));
    }

    @Test
    void shouldMapDependencyFailureToServiceUnavailable() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/tilgang"))
                .withQueryParam("brukerId", equalTo("hashed-id"))
                .willReturn(aResponse().withStatus(500)));

        StepVerifier.create(new GetUsersInSameOrgCommand(
                        webClient,
                        "hashed-id",
                        "exchanged-token").call())
                .expectError(BrukerServiceUnavailableException.class)
                .verify();
    }
}
