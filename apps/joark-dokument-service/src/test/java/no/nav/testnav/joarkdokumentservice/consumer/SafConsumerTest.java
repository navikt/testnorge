package no.nav.testnav.joarkdokumentservice.consumer;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.nav.testnav.joarkdokumentservice.config.Consumers;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

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

    @TempDir
    private Path files;

    private WireMockServer wireMock;

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
        wireMock = new WireMockServer(wireMockConfig().dynamicPort()
                .usingFilesUnderDirectory(files.toString())
                .maxLoggedResponseSize(1024));
        wireMock.start();
        sharedWebClient = new WebClientLogger().webClientBuilder(new JsonMapper()).build();
        when(consumers.getTestnavDollyProxy()).thenReturn(serverProperties);
        when(serverProperties.getUrl()).thenReturn(wireMock.baseUrl());
        safConsumer = new SafConsumer(consumers, tokenExchange, sharedWebClient);
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void shouldReadHundredMiBPdfDespiteInheritedThirtyTwoMiBCodecLimit() throws IOException {
        stubDocument(100 * 1024 * 1024);
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));

        var response = safConsumer.getPDF("journalpost", "dokument", "q2").block(Duration.ofSeconds(30));

        assertThat(response).hasSize(100 * 1024 * 1024);
        var received = Files.write(files.resolve("received.pdf"), response);
        assertThat(Files.mismatch(files.resolve("__files/document.pdf"), received)).isEqualTo(-1);
    }

    @Test
    void shouldRejectPdfLargerThanHundredMiB() throws IOException {
        stubDocument(100 * 1024 * 1024 + 1);
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));

        assertThatThrownBy(() -> safConsumer.getPDF("journalpost", "dokument", "q2").block(Duration.ofSeconds(30)))
                .isInstanceOf(WebClientResponseException.class)
                .hasCauseInstanceOf(DataBufferLimitException.class);
    }

    @Test
    void shouldKeepSharedWebClientBufferLimitUnchanged() throws IOException {
        stubDocument(33 * 1024 * 1024);

        assertThatThrownBy(() -> sharedWebClient.get()
                        .uri(wireMock.baseUrl() + DOCUMENT_PATH)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block(Duration.ofSeconds(30)))
                .isInstanceOf(WebClientResponseException.class)
                .hasCauseInstanceOf(DataBufferLimitException.class);
    }

    private void stubDocument(int size) throws IOException {
        var directory = Files.createDirectories(files.resolve("__files"));
        try (var output = Files.newOutputStream(directory.resolve("document.pdf"))) {
            var chunk = new byte[64 * 1024];
            for (int remaining = size; remaining > 0; remaining -= chunk.length) {
                output.write(chunk, 0, Math.min(remaining, chunk.length));
            }
        }
        wireMock.stubFor(get(urlEqualTo(DOCUMENT_PATH))
                .willReturn(aResponse().withHeader("Content-Type", "application/pdf").withBodyFile("document.pdf")));
    }
}
