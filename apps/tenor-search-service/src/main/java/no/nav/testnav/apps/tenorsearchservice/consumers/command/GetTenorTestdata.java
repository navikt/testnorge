package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.Kilde;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@RequiredArgsConstructor
public class GetTenorTestdata implements Callable<Mono<TenorResponse>> {

    private static final String TENOR_QUERY_URL = "/api/testnorge/v2/soek/{kilde}";

    private final WebClient webClient;
    private final String query;
    private final Kilde kilde;
    private final InfoType type;
    private final String fields;
    private final Integer seed;
    private final String token;

    @Override
    public Mono<TenorResponse> call() {

        log.info("Query-parameter: {}", query);
        var requestParams = Map.of(
                "kilde", getKilde(kilde).getTenorKilde(),
                "query", query,
                "alle", "*");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TENOR_QUERY_URL)
                        .queryParam("kql", "{query}")
                        .queryParam("nokkelinformasjon", isNoekkelinfo(type))
                        .queryParamIfPresent("seed", Optional.ofNullable(seed))
                        .queryParamIfPresent("vis", Optional.ofNullable(getVisning(type)))
                        .queryParamIfPresent("skjul", Optional.ofNullable(getSkjul(type)))
                        .build(requestParams))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> TenorResponse.builder()
                        .status(HttpStatus.OK)
                        .data(response)
                        .query(query)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(TenorResponse.builder()
                        .status(WebClientFilter.getStatus(error))
                        .error(WebClientFilter.getMessage(error))
                        .query(query)
                        .build()));
    }

    private String getSkjul(InfoType type) {

        return isNull(type) || type != InfoType.Kildedata ?
                "*.kildedata" : null;
    }

    private String getVisning(InfoType type) {

        if (nonNull(type)) {
            return switch (type) {
                case Kildedata -> "*.kildedata";
                case AlleFelter -> "{alle}";
                case Spesifikt -> isNotBlank(fields) ? fields : "id";
                default -> null;
            };
        } else {
            return null;
        }
    }

    private Kilde getKilde(Kilde kilde) {

        return isNull(kilde) ? Kilde.FREG : kilde;
    }

    private boolean isNoekkelinfo(InfoType type) {

        return nonNull(type) && type == InfoType.Noekkelinfo;
    }
}