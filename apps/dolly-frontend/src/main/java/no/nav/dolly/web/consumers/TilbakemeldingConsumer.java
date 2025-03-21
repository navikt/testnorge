package no.nav.dolly.web.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.Consumers;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TilbakemeldingConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final AccessService accessService;

    public TilbakemeldingConsumer(
            Consumers consumers,
            AccessService accessService,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnorgeTilbakemeldingApi();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.accessService = accessService;
    }

    public Mono<Void> send(TilbakemeldingDTO dto, ServerWebExchange exchange) {
        return
                accessService.getAccessToken(serverProperties, exchange)
                        .flatMap(token -> webClient
                                .post()
                                .uri("/api/v1/tilbakemelding")
                                .headers(WebClientHeader.bearer(token))
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
