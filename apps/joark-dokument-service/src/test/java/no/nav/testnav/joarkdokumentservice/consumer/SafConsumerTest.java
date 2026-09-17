package no.nav.testnav.joarkdokumentservice.consumer;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.testnav.joarkdokumentservice.config.Consumers;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SafConsumerTest {

    private static final String DOCUMENT_PATH = "/saf/q2/rest/hentdokument/journalpost/dokument/ARKIV";

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

    private WebClient sharedWebClient;
    private SafConsumer safConsumer;

    @BeforeEach
    void setUp() {
        sharedWebClient = new WebClientLogger().webClientBuilder(new JsonMapper()).build();
        when(consumers.getTestnavDollyProxy()).thenReturn(serverProperties);
        when(serverProperties.getUrl()).thenReturn(wireMock.baseUrl());
        safConsumer = new SafConsumer(consumers, tokenExchange, sharedWebClient);
    }

    @Test
    void shouldReadHundredMiBPdfDespiteInheritedThirtyTwoMiBCodecLimit() {
        var document = new byte[100 * 1024 * 1024];
        Arrays.fill(document, (byte) ' ');
        wireMock.stubFor(get(urlEqualTo(DOCUMENT_PATH))
                .willReturn(aResponse().withHeader("Content-Type", "application/pdf").withBody(document)));
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));

        var response = safConsumer.getPDF("journalpost", "dokument", "q2").block(Duration.ofSeconds(30));

        assertThat(Arrays.equals(response, document)).isTrue();
    }

    @Test
    void shouldRejectPdfLargerThanHundredMiB() {
        wireMock.stubFor(get(urlEqualTo(DOCUMENT_PATH))
                .willReturn(aResponse().withHeader("Content-Type", "application/pdf")
                        .withBody(new byte[100 * 1024 * 1024 + 1])));
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));

        assertThatThrownBy(() -> safConsumer.getPDF("journalpost", "dokument", "q2").block(Duration.ofSeconds(30)))
                .isInstanceOf(WebClientResponseException.class)
                .hasCauseInstanceOf(DataBufferLimitException.class);
    }

    @Test
    void shouldKeepSharedWebClientBufferLimitUnchanged() {
        wireMock.stubFor(get(urlEqualTo(DOCUMENT_PATH))
                .willReturn(aResponse().withHeader("Content-Type", "application/pdf")
                        .withBody(new byte[33 * 1024 * 1024])));

        assertThatThrownBy(() -> sharedWebClient.get()
                        .uri(wireMock.baseUrl() + DOCUMENT_PATH)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block(Duration.ofSeconds(30)))
                .isInstanceOf(WebClientResponseException.class)
                .hasCauseInstanceOf(DataBufferLimitException.class);
    }
}
