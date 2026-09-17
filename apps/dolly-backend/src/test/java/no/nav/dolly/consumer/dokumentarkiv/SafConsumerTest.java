package no.nav.dolly.consumer.dokumentarkiv;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SafConsumerTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Mock
    private Consumers consumers;
    @Mock
    private ServerProperties serverProperties;
    @Mock
    private TokenExchange tokenExchange;

    @Test
    void shouldReadExistingFortyMiBDocumentWithoutReturningBufferError() {
        var jsonMapper = new JsonMapper();
        var sharedWebClient = new WebClientLogger().webClientBuilder(jsonMapper).build();
        when(consumers.getTestnavDollyProxy()).thenReturn(serverProperties);
        when(serverProperties.getUrl()).thenReturn(wireMock.baseUrl());
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));
        var safConsumer = new SafConsumer(consumers, tokenExchange, jsonMapper, sharedWebClient);
        var document = " ".repeat(40 * 1024 * 1024);
        wireMock.stubFor(get(urlEqualTo("/saf/q2/rest/hentdokument/journalpost/dokument/ARKIV"))
                .willReturn(aResponse().withHeader("Content-Type", "application/pdf").withBody(document)));

        StepVerifier.create(safConsumer.getDokument("q2", "journalpost", "dokument", "ARKIV"))
                .assertNext(response -> {
                    assertThat(response.getFeilmelding()).isNull();
                    assertThat(document.equals(response.getDokument())).isTrue();
                })
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }
}
