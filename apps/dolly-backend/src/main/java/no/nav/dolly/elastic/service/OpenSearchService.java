package no.nav.dolly.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import no.nav.dolly.elastic.dto.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final RestHighLevelClient restHighLevelClient;

    @Value("${open.search.index}")
    private String index;

    public SearchResponse getTyper(ElasticTyper[] typer) {

        var query = OpenSearchQueryBuilder.buildTyperQuery(typer);
        var response = execQuery(query);

        return getIdenter(response);
    }

    public SearchResponse search(SearchRequest request) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        var response = execQuery(query);

        return getIdenter(response);
    }

    @SneakyThrows
    private org.opensearch.action.search.SearchResponse execQuery(BoolQueryBuilder query)  {

        var searchRequest = new org.opensearch.action.search.SearchRequest(index);
        searchRequest.source(new SearchSourceBuilder().query(query)
                .size(50));

        return  restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
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
                .totalHits(response.getHits().getTotalHits().value)
                .took(response.getTook().getStringRep())
                .score(response.getHits().getMaxScore())
                .build();
    }
}
