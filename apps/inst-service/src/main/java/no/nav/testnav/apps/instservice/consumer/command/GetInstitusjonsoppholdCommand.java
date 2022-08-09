package no.nav.testnav.apps.instservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.domain.InstitusjonResponse;
import no.nav.testnav.apps.instservice.exception.UgyldigIdentResponseException;
import no.nav.testnav.apps.instservice.util.WebClientFilter;
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
public class GetInstitusjonsoppholdCommand implements Callable<Mono<InstitusjonResponse>> {
    private final WebClient webClient;
    private final String token;
    private final String miljoe;
    private final String ident;

    @SneakyThrows
    @Override
    public Mono<InstitusjonResponse> call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/institusjonsopphold/person")
                                    .queryParam("environments", miljoe)
                                    .build()
                    )
                    .header(ACCEPT, "application/json")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .header("norskident", ident)
                    .retrieve()
                    .bodyToMono(InstitusjonResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException));
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke hente ident fra inst2", e);
            throw new UgyldigIdentResponseException("Kunne ikke hente ident fra inst2", e);
        }
    }
}
