package no.nav.testnav.apps.instservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.util.WebClientFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.ACCEPT;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class DeleteInstitusjonsoppholdCommand implements Callable<Mono<ResponseEntity<Object>>> {
    private final WebClient webClient;
    private final String token;
    private final String miljoe;
    private final String ident;

    @SneakyThrows
    @Override
    public Mono<ResponseEntity<Object>> call() {
        try {
            return webClient.delete()
                    .uri(builder ->
                            builder.path("/api/v1/institusjonsopphold/person")
                                    .queryParam("environments", miljoe)
                                    .build()
                    )
                    .header(ACCEPT, "application/json")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .header("norskident", ident)
                    .retrieve()
                    .toEntity(Object.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException));
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke slette institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString()));
        }
    }
}
