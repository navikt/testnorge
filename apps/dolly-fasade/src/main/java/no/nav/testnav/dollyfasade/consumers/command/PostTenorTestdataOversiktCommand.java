package no.nav.testnav.dollyfasade.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollyfasade.dto.TenorRequestDTO;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorOversiktResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostTenorTestdataOversiktCommand implements Callable<Mono<TenorOversiktResponse>> {

    private static final String TENOR_QUERY_URL = "/api/v1/tenor/testdata/oversikt";

    private final WebClient webClient;
    private final TenorRequestDTO request;
    private final String token;

    @Override
    public Mono<TenorOversiktResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TENOR_QUERY_URL)
                        .queryParamIfPresent("antall", Optional.ofNullable(request.getAntall()))
                        .queryParamIfPresent("side", Optional.ofNullable(request.getSide()))
                        .queryParamIfPresent("seed", Optional.ofNullable(request.getSeed()))
                        .queryParamIfPresent("ikkeFiltrer", Optional.ofNullable(request.getIkkeFiltrer()))
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request.getQuery())
                .retrieve()
                .bodyToMono(TenorOversiktResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }
}