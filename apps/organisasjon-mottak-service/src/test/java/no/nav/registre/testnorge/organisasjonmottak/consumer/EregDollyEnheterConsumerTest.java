package no.nav.registre.testnorge.organisasjonmottak.consumer;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.nav.registre.testnorge.organisasjonmottak.config.Consumers;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EregDollyEnheterConsumerTest {

    private WireMockServer wireMockServer;
    private EregDollyEnheterConsumer consumer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();

        consumer = consumer(Duration.ofSeconds(5), Duration.ZERO);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldSendOtherMiljoeWhenOneMiljoeFails() {
        wireMockServer.stubFor(post(urlEqualTo("/ereg-dolly/q1/api/start"))
                .willReturn(aResponse().withStatus(429)));
        wireMockServer.stubFor(post(urlEqualTo("/ereg-dolly/q2/api/start"))
                .willReturn(aResponse().withStatus(202)));

        StepVerifier.create(Flux.merge(
                        consumer.send(Flatfil.create(), "q1"),
                        consumer.send(Flatfil.create(), "q2")))
                .verifyComplete();

        wireMockServer.verify(2, postRequestedFor(urlEqualTo("/ereg-dolly/q1/api/start")));
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/ereg-dolly/q2/api/start")));
    }

    @Test
    void shouldCompleteWhenTidsavbruddIsReached() {
        wireMockServer.stubFor(post(urlEqualTo("/ereg-dolly/q1/api/start"))
                .willReturn(aResponse().withStatus(202).withFixedDelay(2_000)));

        var consumerMedKortTidsavbrudd = consumer(Duration.ofMillis(200), Duration.ZERO);

        StepVerifier.create(consumerMedKortTidsavbrudd.send(Flatfil.create(), "q1"))
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }

    @Test
    void shouldPauseAfterStartBeforeCompleting() {
        wireMockServer.stubFor(post(urlEqualTo("/ereg-dolly/q1/api/start"))
                .willReturn(aResponse().withStatus(202)));

        var consumerMedPause = consumer(Duration.ofSeconds(5), Duration.ofMillis(500));

        var varighet = StepVerifier.create(consumerMedPause.send(Flatfil.create(), "q1"))
                .verifyComplete();

        assertThat(varighet).isGreaterThanOrEqualTo(Duration.ofMillis(500));
    }

    private EregDollyEnheterConsumer consumer(Duration tidsavbrudd, Duration pauseEtterStart) {
        var eregDollyEnheterConsumer = new EregDollyEnheterConsumer(mockConsumers(), mockTokenExchange(), WebClient.builder().build());
        ReflectionTestUtils.setField(eregDollyEnheterConsumer, "maxNyeForsoek", 1L);
        ReflectionTestUtils.setField(eregDollyEnheterConsumer, "ventetid", Duration.ofMillis(10));
        ReflectionTestUtils.setField(eregDollyEnheterConsumer, "svartid", Duration.ofSeconds(5));
        ReflectionTestUtils.setField(eregDollyEnheterConsumer, "tidsavbrudd", tidsavbrudd);
        ReflectionTestUtils.setField(eregDollyEnheterConsumer, "pauseEtterStart", pauseEtterStart);
        return eregDollyEnheterConsumer;
    }

    private Consumers mockConsumers() {
        var consumers = mock(Consumers.class);
        when(consumers.getTestnavDollyProxy())
                .thenReturn(ServerProperties.of("dev-fss", "dolly", "testnav-dolly-proxy", wireMockServer.baseUrl()));
        return consumers;
    }

    private static TokenExchange mockTokenExchange() {
        var tokenExchange = mock(TokenExchange.class);
        when(tokenExchange.exchange(any())).thenReturn(Mono.just(new AccessToken("token")));
        return tokenExchange;
    }
}
