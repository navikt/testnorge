package no.nav.dolly.bestilling.arbeidssoekerregisteret;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ArbeidssoekerregisteretConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static WireMockServer wireMockServer;

    static {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {

        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        registry.add("consumers.arbeidssoekerregisteret-proxy.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @Autowired
    private ArbeidssoekerregisteretConsumer arbeidssoekerregisteretConsumer;

    @Test
    void deleteArbeidssoekerregisteret() {

        stubFor(delete(urlPathEqualTo("/api/v1/arbeidssoekerregistrering/" + IDENT))
                .willReturn(ok()));

        StepVerifier.create(arbeidssoekerregisteretConsumer.deleteArbeidssokerregisteret(IDENT))
                .assertNext(status -> assertThat(status).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }
}
