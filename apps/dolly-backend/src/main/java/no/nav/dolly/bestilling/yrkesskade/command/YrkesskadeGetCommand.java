package no.nav.dolly.bestilling.yrkesskade.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.yrkesskade.dto.SaksoversiktDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class YrkesskadeGetCommand implements Callable<Mono<SaksoversiktDTO>> {

    private static final String YRKESSKADE_URL = "/api/v1/yrkesskader/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<SaksoversiktDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(YRKESSKADE_URL)
                        .build(ident))
                .accept(MediaType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .header("ident", ident)
                .retrieve()
                .bodyToMono(SaksoversiktDTO.class)
                .map(resultat -> SaksoversiktDTO.builder()
                        .status(HttpStatusCode.valueOf(200))
                        .saker(resultat.getSaker())
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> SaksoversiktDTO.of(WebClientError.describe(throwable)));
    }
}