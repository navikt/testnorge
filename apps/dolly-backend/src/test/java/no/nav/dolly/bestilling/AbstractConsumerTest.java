package no.nav.dolly.bestilling;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@SpringJUnitConfig
@ExtendWith(WireMockExtension.class)
public abstract class AbstractConsumerTest {

    protected static WireMockServer wireMockServer;

    protected WebClient webClient;

    @Mock
    protected TokenExchange tokenExchange;

    @Mock
    protected Consumers consumers;

    @Mock
    protected ServerProperties serverProperties;

    @Mock
    protected WebClientLogger webClientLogger;

    @BeforeAll
    static void setUpBeforeAll() {

        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDownAfterAll() {

        wireMockServer.shutdown();
    }

    @BeforeEach
    void setUp() {

        wireMockServer.resetAll();
        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .build();
        when(tokenExchange.exchange(any())).thenReturn(Mono.just(new AccessToken("token")));
    }
}
