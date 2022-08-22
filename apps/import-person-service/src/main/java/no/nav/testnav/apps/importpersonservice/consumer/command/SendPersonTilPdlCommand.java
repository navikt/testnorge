package no.nav.testnav.apps.importpersonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SendPersonTilPdlCommand implements Callable<Mono<OrdreResponseDTO>> {
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<OrdreResponseDTO> call() {
        log.info("Sender person {} til PDL.", ident);
        return webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v1/personer/{ident}/ordre")
                        .build(ident)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OrdreResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
