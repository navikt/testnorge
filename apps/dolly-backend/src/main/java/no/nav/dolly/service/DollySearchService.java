package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.dollysearchservice.DollySearchServiceConsumer;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.service.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollySearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final DollySearchServiceConsumer dollySearchServiceConsumer;

    @Value("${open.search.index}")
    private String index;

    public Mono<SearchResponse> search(List<ElasticTyper> registre, SearchRequest request) {

        if (nonNull(registre)) {
            var query = OpenSearchQueryBuilder.buildTyperQuery(registre.toArray(ElasticTyper[]::new));
            var registreResultat = execQuery(query);
            request.setIdenter(new HashSet<>(!registreResultat.getIdenter().isEmpty() ?
                    registreResultat.getIdenter() : List.of("99999999999")));
        }
        return dollySearchServiceConsumer.doPersonSearch(request);
    }

    private no.nav.dolly.elastic.dto.SearchResponse execQuery(BoolQueryBuilder query) {

        var searchRequest = new org.opensearch.action.search.SearchRequest(index);
        searchRequest.source(new SearchSourceBuilder().query(query)
                .size(500));

        try {
            var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return getIdenter(response);

        } catch (IOException e) {
            log.error("OpenSearch feil ved utføring av søk: {}", e.getMessage(), e);
            return no.nav.dolly.elastic.dto.SearchResponse.builder()
                    .error(e.getLocalizedMessage())
                    .build();
        }
    }

    private static no.nav.dolly.elastic.dto.SearchResponse getIdenter(org.opensearch.action.search.SearchResponse response) {

        return no.nav.dolly.elastic.dto.SearchResponse.builder()
                .identer(Arrays.stream(response.getHits().getHits())
                        .map(SearchHit::getSourceAsMap)
                        .map(map -> (List<String>) map.get("identer"))
                        .flatMap(Collection::stream)
                        .distinct()
                        .limit(100)
                        .toList())
                .totalHits(getTotalHits(response.getHits()))
                .took(response.getTook().getStringRep())
                .score(response.getHits().getMaxScore())
                .build();
    }

    @SuppressWarnings("java:S2259")
    private static Long getTotalHits(SearchHits searchHits) {

        return nonNull(searchHits) && nonNull(searchHits.getTotalHits()) ?
                searchHits.getTotalHits().value : null;
    }
}
