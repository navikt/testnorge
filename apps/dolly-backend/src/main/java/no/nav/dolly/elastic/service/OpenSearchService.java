package no.nav.dolly.elastic.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.consumer.ElasticParamsConsumer;
import no.nav.dolly.elastic.dto.SearchRequest;
import no.nav.dolly.elastic.dto.SearchResponse;
import no.nav.dolly.elastic.dto.Kategori;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final ElasticParamsConsumer elasticParamsConsumer;
    private final BestillingElasticRepository bestillingElasticRepository;

    @Value("${open.search.index}")
    private String index;

    public SearchResponse getTyper(ElasticTyper[] typer) {

        var query = OpenSearchQueryBuilder.buildTyperQuery(typer);
        return execQuery(query);
    }

    public SearchResponse search(SearchRequest request) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        return execQuery(query);
    }

    public List<ElasticBestilling> search(String ident) {

        return bestillingElasticRepository.getAllByIdenterIn(List.of(ident));
    }

    public Mono<JsonNode> deleteIndex() {

        return elasticParamsConsumer.deleteIndex();
    }

    public List<Kategori> getTyper() {

        return Stream.of(ElasticTyper.values())
                .map(entry -> Kategori.builder()
                        .type(entry.name())
                        .beskrivelse(entry.getBeskrivelse())
                        .build())
                .sorted(Comparator.comparing(Kategori::getBeskrivelse))
                .toList();
    }

    private SearchResponse execQuery(BoolQueryBuilder query) {

        var searchRequest = new org.opensearch.action.search.SearchRequest(index);
        searchRequest.source(new SearchSourceBuilder().query(query)
                .size(50));

        try {
            var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return getIdenter(response);

        } catch (IOException e) {
            log.error("OpenSearch feil ved utføring av søk: {}", e.getMessage(), e);
            return SearchResponse.builder()
                    .error(e.getLocalizedMessage())
                    .build();
        }
    }

    private static SearchResponse getIdenter(org.opensearch.action.search.SearchResponse response) {

        return SearchResponse.builder()
                .identer(Arrays.stream(response.getHits().getHits())
                        .map(SearchHit::getSourceAsMap)
                        .map(map -> (List<String>) map.get("identer"))
                        .flatMap(Collection::stream)
                        .distinct()
                        .limit(10)
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
