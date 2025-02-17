package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.ElasticSearchConsumer;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final ElasticSearchConsumer elasticSearchConsumer;
    private final ObjectMapper objectMapper;

    public Mono<SearchResponse> search(SearchRequest request, Integer side, Integer antall, Integer seed) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request, seed);
        return execQuery(query, side, antall);
    }

    private Mono<SearchResponse> execQuery(BoolQueryBuilder query, Integer side, Integer antall) {

        return Mono.from(elasticSearchConsumer.search(new org.opensearch.action.search.SearchRequest()
                        .indices("pdl-sok")
                        .source(new SearchSourceBuilder()
                                .from(nonNull(side) ? side : 0)
                                .query(query)
                                .size(nonNull(antall) ? antall : 10)
                                .timeout(new TimeValue(3, TimeUnit.SECONDS))))
                .map(this::getIdenter));
    }


    private SearchResponse getIdenter(no.nav.testnav.dollysearchservice.model.SearchResponse response) {

        if (isNotBlank(response.getError())) {
            return SearchResponse.builder()
                    .error(response.getError())
                    .build();
        }

        return SearchResponse.builder()
                .took(response.getTook().toString())
                .totalHits(response.getHits().getTotal().getValue())
                .score(response.getHits().getMaxScore())
                .personer(response.getHits().getHits().stream()
                        .map(no.nav.testnav.dollysearchservice.model.SearchResponse.SearchHit::get_source)
                        .map(person -> objectMapper.convertValue(person, JsonNode.class))
//                        .map(result -> objectMapper.convertValue(result, Response.class))
//                        .map(Response::getHentIdenter)
//                        .map(HentIdenterModel::getIdenter)
//                        .flatMap(Collection::stream)
//                        .filter(IdenterModel::isFolkeregisterident)
//                        .map(IdenterModel::getIdent)
                        .toList())
                .build();
    }

    @SuppressWarnings("java:S2259")
    private static Long getTotalHits(SearchHits searchHits) {

        return nonNull(searchHits) && nonNull(searchHits.getTotalHits()) ?
                searchHits.getTotalHits().value : null;
    }
}
