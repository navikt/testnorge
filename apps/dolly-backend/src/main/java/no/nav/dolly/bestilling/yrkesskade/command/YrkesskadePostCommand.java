package no.nav.dolly.bestilling.yrkesskade.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.yrkesskade.dto.YrkesskadeResponseDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class YrkesskadePostCommand implements Callable<Mono<YrkesskadeResponseDTO>> {

    private static final String YRKESSKADE_URL = "/api/v1/yrkesskader";

    private final WebClient webClient;
    private final YrkesskadeRequest yrkesskadeRequest;
    private final String token;

    @Override
    public Mono<YrkesskadeResponseDTO> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(YRKESSKADE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ident", yrkesskadeRequest.getInnmelderIdentifikator())
                .bodyValue(yrkesskadeRequest)
                .retrieve()
                .toBodilessEntity()
                .map(response -> YrkesskadeResponseDTO.builder()
                        .status(HttpStatusCode.valueOf(201))
                        .build())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(throwable -> Mono.just(YrkesskadeResponseDTO.builder()
                        .status(WebClientError.describe(throwable).getStatus())
                        .melding(WebClientError.describe(throwable).getMessage())
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}