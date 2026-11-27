package no.nav.testnav.dollyfasade.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollyfasade.dto.TenorRequestDTO;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostTenorTestdataCommand implements Callable<Mono<TenorResponse>> {

    private static final String TENOR_QUERY_URL = "/api/v1/tenor/testdata";

    private final WebClient webClient;
    private final TenorRequestDTO request;
    private final String token;

    @Override
    public Mono<TenorResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TENOR_QUERY_URL)
                        .queryParamIfPresent("type", Optional.ofNullable(request.getType()))
                        .queryParamIfPresent("fields", Optional.ofNullable(request.getFields()))
                        .queryParamIfPresent("antall", Optional.ofNullable(request.getAntall()))
                        .queryParamIfPresent("side", Optional.ofNullable(request.getSide()))
                        .queryParamIfPresent("seed", Optional.ofNullable(request.getSeed()))
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request.getQuery())
                .retrieve()
                .bodyToMono(TenorResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }
}