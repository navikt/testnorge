package no.nav.dolly.web.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.credentials.TestnorgeTilbakemeldingApiProperties;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TilbakemeldingConsumer {
    private final WebClient webClient;
    private final TestnorgeTilbakemeldingApiProperties testnorgeTilbakemeldingApiProperties;
    private final AccessService accessService;

    public TilbakemeldingConsumer(
            TestnorgeTilbakemeldingApiProperties tilbakemeldingApiProperties,
            AccessService accessService,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.testnorgeTilbakemeldingApiProperties = tilbakemeldingApiProperties;
        this.webClient = WebClient.builder()
                .baseUrl(tilbakemeldingApiProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
        this.accessService = accessService;
    }

    public Mono<Void> send(TilbakemeldingDTO dto, ServerWebExchange exchange) {
        return
                accessService.getAccessToken(testnorgeTilbakemeldingApiProperties, exchange)
                        .flatMap(accessToken -> webClient
                                .post()
                                .uri("/api/v1/tilbakemelding")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(BodyInserters.fromPublisher(Mono.just(dto), TilbakemeldingDTO.class))
                                .retrieve()
                                .bodyToMono(Void.class)
                                .doOnError(error -> {
                                    if (error instanceof WebClientResponseException webClientResponseException) {
                                        log.error(
                                                "Feil ved innsendelse av tilbakemelding: {}.",
                                                webClientResponseException.getResponseBodyAsString(),
                                                error
                                        );
                                    } else {
                                        log.error("Feil ved innsendelse av tilbakemelding.", error);
                                    }
                                })
                        );
    }
}
