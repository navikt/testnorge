package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.net.ConnectException;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

class StartEregDollyEnheterCommandTest {

    private static final String START_URL = "/ereg-dolly/q2/api/start";
    private static final byte[] FLATFIL = "HEADER\nENH ØKONOMI\nTRAILER\n".getBytes(ISO_8859_1);

    private WireMockServer wireMockServer;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        webClient = WebClient.builder().build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldPostFlatfilAsOctetStreamWithBearerToken() {
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .willReturn(aResponse().withStatus(202)));

        StepVerifier.create(command(3).call())
                .verifyComplete();

        wireMockServer.verify(1, postRequestedFor(urlEqualTo(START_URL))
                .withHeader(AUTHORIZATION, equalTo("Bearer token"))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_OCTET_STREAM_VALUE))
                .withRequestBody(binaryEqualTo(FLATFIL)));
    }

    @Test
    void shouldRetryWhenJobIsAlreadyRunning() {
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .inScenario("jobb-i-gang")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "30"))
                .willSetStateTo("ledig"));
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .inScenario("jobb-i-gang")
                .whenScenarioStateIs("ledig")
                .willReturn(aResponse().withStatus(202)));

        StepVerifier.create(command(3).call())
                .verifyComplete();

        wireMockServer.verify(2, postRequestedFor(urlEqualTo(START_URL)));
    }

    @Test
    void shouldFailWhenJobKeepsRunning() {
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .willReturn(aResponse().withStatus(429)));

        StepVerifier.create(command(2).call())
                .expectError(WebClientResponseException.TooManyRequests.class)
                .verify();

        wireMockServer.verify(3, postRequestedFor(urlEqualTo(START_URL)));
    }

    @Test
    void shouldShareRetryBudgetBetweenServerErrorsAndJobInProgress() {
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .inScenario("blandet")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("feil"));
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .inScenario("blandet")
                .whenScenarioStateIs("feil")
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo("i-gang"));
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .inScenario("blandet")
                .whenScenarioStateIs("i-gang")
                .willReturn(aResponse().withStatus(429)));

        StepVerifier.create(command(3).call())
                .expectError(WebClientResponseException.TooManyRequests.class)
                .verify();

        wireMockServer.verify(4, postRequestedFor(urlEqualTo(START_URL)));
    }

    @Test
    void shouldNotRetryWhenResponseTimesOutAfterRequestWasSent() {
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .willReturn(aResponse().withStatus(202).withFixedDelay(1_000)));

        StepVerifier.create(command(3, Duration.ofMillis(200)).call())
                .expectError(WebClientRequestException.class)
                .verify();

        wireMockServer.verify(1, postRequestedFor(urlEqualTo(START_URL)));
    }

    @Test
    void shouldRetryWhenConnectionIsRefused() {
        var lukketPort = new WireMockServer(wireMockConfig().dynamicPort());
        lukketPort.start();
        var url = lukketPort.baseUrl();
        lukketPort.stop();

        var command = new StartEregDollyEnheterCommand(WebClient.builder().build(), url,
                "token", "q2", FLATFIL, 2, Duration.ofMillis(10), Duration.ofSeconds(5));

        StepVerifier.create(command.call())
                .expectErrorMatches(error -> error instanceof WebClientRequestException &&
                        error.getCause() instanceof ConnectException)
                .verify();
    }

    @Test
    void shouldNotRetryOnClientError() {
        wireMockServer.stubFor(post(urlEqualTo(START_URL))
                .willReturn(aResponse().withStatus(400)));

        StepVerifier.create(command(3).call())
                .expectError(WebClientResponseException.BadRequest.class)
                .verify();

        wireMockServer.verify(1, postRequestedFor(urlEqualTo(START_URL)));
    }

    private StartEregDollyEnheterCommand command(long maxForsoek) {
        return command(maxForsoek, Duration.ofSeconds(5));
    }

    private StartEregDollyEnheterCommand command(long maxForsoek, Duration svartid) {
        return new StartEregDollyEnheterCommand(webClient, wireMockServer.baseUrl(), "token", "q2", FLATFIL, maxForsoek, Duration.ofMillis(10), svartid);
    }
}
