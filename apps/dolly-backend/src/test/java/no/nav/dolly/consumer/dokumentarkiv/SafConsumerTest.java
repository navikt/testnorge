package no.nav.dolly.consumer.dokumentarkiv;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SafConsumerTest {

    @TempDir
    private Path files;

    private WireMockServer wireMock;

    @Mock
    private Consumers consumers;
    @Mock
    private ServerProperties serverProperties;
    @Mock
    private TokenExchange tokenExchange;

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(wireMockConfig().dynamicPort()
                .usingFilesUnderDirectory(files.toString())
                .maxLoggedResponseSize(1024));
        wireMock.start();
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void shouldReadExistingHundredMiBDocumentWithoutReturningBufferError() throws IOException {
        var jsonMapper = new JsonMapper();
        var sharedWebClient = new WebClientLogger().webClientBuilder(jsonMapper).build();
        when(consumers.getTestnavDollyProxy()).thenReturn(serverProperties);
        when(serverProperties.getUrl()).thenReturn(wireMock.baseUrl());
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));
        var safConsumer = new SafConsumer(consumers, tokenExchange, jsonMapper, sharedWebClient);
        var directory = Files.createDirectories(files.resolve("__files"));
        try (var output = Files.newOutputStream(directory.resolve("document.pdf"))) {
            var chunk = new byte[1024 * 1024];
            for (int i = 0; i < 100; i++) {
                output.write(chunk);
            }
        }
        wireMock.stubFor(get(urlEqualTo("/saf/q2/rest/hentdokument/journalpost/dokument/ARKIV"))
                .willReturn(aResponse().withHeader("Content-Type", "application/pdf").withBodyFile("document.pdf")));

        StepVerifier.create(safConsumer.getDokument("q2", "journalpost", "dokument", "ARKIV"))
                .assertNext(response -> {
                    assertThat(response.getFeilmelding()).isNull();
                    assertThat(response.getDokument()).hasSize(100 * 1024 * 1024);
                    assertThat(response.getDokument().chars().allMatch(value -> value == 0)).isTrue();
                })
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }
}
