package no.nav.organisasjonforvalter.consumer.command;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class OrganisasjonServiceCommandTest {

    private static final String URL = "/api/v1/organisasjoner/123456789";

    private WireMockServer wireMockServer;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        webClient = WebClient.builder().baseUrl(wireMockServer.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnEmptyWhenOrganisasjonIsNotFound() {
        wireMockServer.stubFor(get(urlEqualTo(URL)).willReturn(aResponse().withStatus(404)));

        assertThat(new OrganisasjonServiceCommand(webClient, "123456789", "q2", "token").call().collectList().block())
                .isEmpty();
    }

    @Test
    void shouldReturnErrorWithoutWaitingWhenServerFails() {
        wireMockServer.stubFor(get(urlEqualTo(URL)).willReturn(aResponse().withStatus(503)));

        assertThat(new OrganisasjonServiceCommand(webClient, "123456789", "q2", "token").call().collectList().block())
                .singleElement()
                .satisfies(organisasjon -> assertThat(organisasjon.getError()).isNotBlank());

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(URL)));
    }

    @Test
    void shouldReturnErrorWhenOppslagFails() {
        wireMockServer.stubFor(get(urlEqualTo(URL)).willReturn(aResponse().withStatus(400)));

        assertThat(new OrganisasjonServiceCommand(webClient, "123456789", "q2", "token").call().collectList().block())
                .singleElement()
                .satisfies(organisasjon -> {
                    assertThat(organisasjon.getError()).isNotBlank();
                    assertThat(organisasjon.getOrgnummer()).isNull();
                });
    }
}
