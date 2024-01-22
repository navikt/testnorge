package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class GetTenorTestdata implements Callable<Mono<TenorResponse>> {

    private static final String TENOR_QUERY_URL = "/api/testnorge/v2/soek/{kilde}";
    private static final String TENOR_UTVIDET_QUERY_URL = "/api/testnorge/v2/soek/{kilde}/utvidet";

    private final WebClient webClient;
    private final String query;
    private final InfoType type;
    private final String token;

    @Override
    public Mono<TenorResponse> call() {

        log.info("Query-parameter: {}", query);
        var requestParams = Map.of("tenorQuery", query,
                "kilde", "freg");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(getUrl(type))
                        .queryParam("kql", "{tenorQuery}")
                        .queryParam("nokkelinformasjon", isNoekkelinfo(type))
                        .build(requestParams))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> TenorResponse.builder()
                        .status(HttpStatus.OK)
                        .data(response)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(TenorResponse.builder()
                        .status(WebClientFilter.getStatus(error))
                        .error(WebClientFilter.getMessage(error))
                        .build()));
    }

    private String getUrl(InfoType type) {

        return isNull(type) || type != InfoType.Kildedokument ? TENOR_QUERY_URL : TENOR_UTVIDET_QUERY_URL;
    }

    private boolean isNoekkelinfo(InfoType type) {

        return nonNull(type) && type == InfoType.Noekkelinfo;
    }
}