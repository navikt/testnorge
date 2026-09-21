package no.nav.dolly.bestilling.dokarkiv;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.netty.handler.timeout.ReadTimeoutException;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivPostCommand;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DokarkivConsumerTest {

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
    void shouldAcceptJournalpostResponseAfterOneMinute() {
        var consumer = createConsumer();
        wireMock.stubFor(post(urlPathEqualTo("/dokarkiv/api/q2/v1/journalpost"))
                .willReturn(okJson("{\"journalpostId\":\"journalpost\",\"journalpostferdigstilt\":false}")
                        .withFixedDelay(61_000)));

        StepVerifier.create(consumer.postDokarkiv("q2", new DokarkivRequest()))
                .assertNext(response -> {
                    assertThat(response.getFeilmelding()).isNull();
                    assertThat(response.getJournalpostId()).isEqualTo("journalpost");
                    assertThat(response.getMiljoe()).isEqualTo("q2");
                })
                .expectComplete()
                .verify(Duration.ofSeconds(75));
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

    private DokarkivConsumer createConsumer() {
        when(consumers.getTestnavDollyProxy()).thenReturn(serverProperties);
        when(serverProperties.getUrl()).thenReturn(wireMock.baseUrl());
        when(tokenExchange.exchange(serverProperties)).thenReturn(Mono.just(new AccessToken("test-token")));
        return new DokarkivConsumer(consumers, tokenExchange, new JsonMapper(), WebClient.create());
    }
}
