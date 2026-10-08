package no.nav.dolly.bestilling.dokarkiv;

import io.netty.handler.timeout.ReadTimeoutException;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivPostCommand;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DokarkivConsumerTest extends AbstractConsumerTest {

    @Autowired
    private DokarkivConsumer dokarkivConsumer;

    @Test
    void shouldAcceptDelayedJournalpostResponse() {
        stubFor(post(urlPathMatching("(.*)/dokarkiv/api/q2/v1/journalpost"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"journalpostId\":\"journalpost\",\"journalpostferdigstilt\":false}")
                        .withFixedDelay(100)));

        StepVerifier.create(dokarkivConsumer.postDokarkiv("q2", new DokarkivRequest()))
                .assertNext(response -> {
                    assertThat(response.getFeilmelding()).isNull();
                    assertThat(response.getJournalpostId()).isEqualTo("journalpost");
                    assertThat(response.getMiljoe()).isEqualTo("q2");
                })
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void shouldReportJournalpostReadTimeoutWithoutRetrying() {
        var exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any())).thenReturn(Mono.error(new WebClientRequestException(
                ReadTimeoutException.INSTANCE, HttpMethod.POST, URI.create("http://localhost"), new HttpHeaders())));
        var webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();
        var command = new DokarkivPostCommand(webClient, "q2", new DokarkivRequest(), "test-token");

        StepVerifier.create(command.call())
                .assertNext(response -> assertThat(response.getFeilmelding())
                        .isEqualTo("Mottaker svarer ikke, eller har for lang svartid."))
                .expectComplete()
                .verify(Duration.ofSeconds(5));

        verify(exchangeFunction).exchange(any());
    }
}
