package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.DollyBackendConsumer;
import no.nav.testnav.dollysearchservice.consumer.OpenSearchConsumer;
import no.nav.testnav.dollysearchservice.dto.DollyBackendSelector;
import no.nav.testnav.dollysearchservice.dto.OpenSearchResponse;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final DollyBackendConsumer dollyBackendConsumer;
    private final OpenSearchConsumer openSearchConsumer;
    private final ObjectMapper objectMapper;

    public Mono<SearchResponse> search(SearchRequest request, DollyBackendSelector selector, Boolean ikkeFiltrer) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        return Mono.zip(
                        dollyBackendConsumer.getFinnesInfo(request.getIdenter(), selector)
                                .doOnNext(dolly -> log.info("FinnesIDollyGetCommand: {}", dolly)),
                        execQuery(request, query))
                .map(tuple -> {
                    var personer = tuple.getT2().getPersoner().entrySet().stream()
                            .filter(person -> isFalse(ikkeFiltrer) ||
                                    request.getIdenter().isEmpty() ||
                                    isTrue(tuple.getT1().getIBruk().get(person.getKey())))
                            .map(Map.Entry::getValue)
                            .toList();
                    return SearchResponse.builder()
                            .seed(tuple.getT2().getSeed())
                            .took(tuple.getT2().getTook())
                            .totalHits(tuple.getT2().getTotalHits())
                            .side(tuple.getT2().getSide())
                            .error(tuple.getT2().getError())
                            .personer(personer)
                            .antall(personer.size())
                            .build();
                });
    }

    private Mono<no.nav.testnav.dollysearchservice.dto.SearchResponse> execQuery(SearchRequest request, BoolQueryBuilder query) {

        if (isNull(request.getSide())) {
            request.setSide(1);
        }

        return Mono.from(openSearchConsumer.search(
                        no.nav.testnav.dollysearchservice.dto.SearchRequest.builder()
                                .query(
                                        new org.opensearch.action.search.SearchRequest()
                                                .indices("pdl-sok")
                                                .source(new SearchSourceBuilder()
                                                        .query(query)
                                                        .from(request.getSide())
                                                        .size(nonNull(request.getAntall()) ? request.getAntall() : 10)
                                                        .timeout(new TimeValue(3, TimeUnit.SECONDS))))
                                .request(request)
                                .build()))
                .map(this::formatResponse);
    }

    private no.nav.testnav.dollysearchservice.dto.SearchResponse formatResponse(OpenSearchResponse response) {

        if (isNotBlank(response.getError())) {
            return no.nav.testnav.dollysearchservice.dto.SearchResponse.builder()
                    .error(response.getError())
                    .build();
        }

        return no.nav.testnav.dollysearchservice.dto.SearchResponse.builder()
                .took(response.getTook().toString())
                .totalHits(response.getHits().getTotal().getValue())
                .antall(response.getHits().getHits().size())
                .side(response.getRequest().getSide())
                .seed(response.getRequest().getSeed())
                .personer(response.getHits().getHits().stream()
                        .map(OpenSearchResponse.SearchHit::get_source)
                        .collect(Collectors.toMap(OpenSearchService::getIdent,
                                person -> objectMapper.convertValue(person, JsonNode.class))))
                .build();
    }

    private static String getIdent(Object source) {
        return Optional.of(source)
                .map(person -> ((Map) source).get("hentIdenter"))
                .map(hentIdenter -> ((Map) hentIdenter).get("identer"))
                .flatMap(identer -> ((List<Map<String, String>>) identer).stream()
                        .filter(ident -> !((Map) ident).get("gruppe").equals("AKTORID") &&
                                isFalse((Boolean) ((Map) ident).get("historisk")))
                        .findFirst())
                .map(ident -> ident.get("ident"))
                .orElse(null);
    }
}
