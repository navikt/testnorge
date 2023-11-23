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
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final RestHighLevelClient restHighLevelClient;

    public SearchResponse getAll(SearchRequest request) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);

        return execQuery(query);
    }

    public SearchResponse getTyper(ElasticTyper[] typer) {

        var query = OpenSearchQueryBuilder.buildTyperQuery(typer);

        return execQuery(query);
    }

    @SneakyThrows
    private SearchResponse execQuery(BoolQueryBuilder query)  {

        var searchRequest = new org.opensearch.action.search.SearchRequest("bestilling");
        searchRequest.source(new SearchSourceBuilder().query(query)
                .size(50));

        var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return SearchResponse.builder()
                .identer(Arrays.stream(response.getHits().getHits())
                        .map(SearchHit::getSourceAsMap)
                        .map(map -> (List<String>) map.get("identer"))
                        .flatMap(Collection::stream)
                        .distinct()
                        .limit(10)
                        .toList())
                .totalHits(response.getHits().getTotalHits().value)
                .build();
    }
}
