package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.util.WebClientFilter;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetSamhandlerCommand implements Callable<Mono<List<SamhandlerDTO>>> {

    private static final ParameterizedTypeReference<List<SamhandlerDTO>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private final String ident;
    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<SamhandlerDTO>> call() {
        log.info("Henter samhandlerinformasjon for ident {}", ident);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/sar/rest/v2/samh")
                        .queryParam("ident", ident)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(REQUEST_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> {
                    log.error("Feil ved henting av samhandlerinformasjon til ident {}", ident);
                    WebClientFilter.logErrorMessage(throwable);
                    return Mono.just(Collections.emptyList());
                });
    }
}
