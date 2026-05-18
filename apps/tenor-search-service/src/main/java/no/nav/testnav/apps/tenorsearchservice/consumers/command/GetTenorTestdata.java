package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.Kilde;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private final Integer antall;
    private final Integer side;
    private final Integer seed;
    private final String token;

    @Override
    public Mono<TenorResponse> call() {

        log.info("Query-parameter: {}", query);
        var requestParams = Map.of(
                "kilde", kilde.getTenorKilde(),
                "query", query,
                "alle", "*");

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TENOR_QUERY_URL)
                        .queryParam("kql", "{query}")
                        .queryParam("nokkelinformasjon", isNoekkelinfo(type))
                        .queryParamIfPresent("seed", Optional.ofNullable(seed))
                        .queryParamIfPresent("vis", Optional.ofNullable(getVisning(type)))
                        .queryParamIfPresent("skjul", Optional.ofNullable(getSkjul(type)))
                        .queryParamIfPresent("antall", Optional.ofNullable(getAntall(antall, type)))
                        .queryParamIfPresent("side", Optional.ofNullable(side))
                        .build(requestParams))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> TenorResponse
                            .builder()
                            .status(HttpStatus.OK)
                            .data(response)
                            .query(query)
                            .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> TenorResponse.of(WebClientError.describe(error), query));
    }

    private Integer getAntall(Integer antall, InfoType type) {

        if (nonNull(antall)) {
            return antall;
        }

        return nonNull(type) ?
                switch (type) {
                    case DataFelter, AlleFelter, Kildedata -> 1;
                    case IdentOgNavn, Organisasjon -> 10;
                    default -> null;
                } : null;
    }

    private String getSkjul(InfoType type) {

        return isNull(type) || (type != InfoType.Kildedata && type != InfoType.AlleFelter) ?
                "*.kildedata" : null;
    }

    private String getVisning(InfoType type) {

        if (nonNull(type)) {
            return switch (type) {
                case IdentOgNavn -> "id,fornavn,etternavn,tenorRelasjoner.*.tenorRelasjonsnavn";
                case Organisasjon -> "visningnavn,tenorMetadata,navn";
                case Kildedata -> "*.kildedata";
                case AlleFelter, DataFelter -> "{alle}";
                case Spesifikt -> isNotBlank(fields) ? fields : "id";
                default -> "identifikator";
            };
        } else {
            return "identifikator";
        }
    }

    private boolean isNoekkelinfo(InfoType type) {

        return nonNull(type) && type == InfoType.Noekkelinfo;
    }
}