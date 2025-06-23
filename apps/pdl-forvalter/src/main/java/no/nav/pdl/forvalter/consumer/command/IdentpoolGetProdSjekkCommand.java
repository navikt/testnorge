package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.ProdSjekkDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolGetProdSjekkCommand implements Callable<Flux<ProdSjekkDTO>> {

    private static final String PROD_SJEKK_URL = "/api/v1/identifikator/prod-sjekk";

    private final WebClient webClient;
    private final Set<String> identer;
    private final String token;

    @Override
    public Flux<ProdSjekkDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(PROD_SJEKK_URL)
                        .queryParam("identer", identer)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(ProdSjekkDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Flux.fromIterable(identer)
                        .map(ident -> ProdSjekkDTO.builder()
                                .ident(ident)
                                .inUse(true)
                                .available(false)
                                .build()));
    }
}
