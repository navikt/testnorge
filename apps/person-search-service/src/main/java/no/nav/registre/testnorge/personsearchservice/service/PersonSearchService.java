package no.nav.registre.testnorge.personsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.personsearchservice.domain.Search;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonSearchService {
    private final RestHighLevelClient client;


    @SneakyThrows
    public void search(Search search){
        var query = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("tags", "DOLLY"));

        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");

        var searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        log.info("Number of hits: {}", searchResponse.getHits().getTotalHits().value);
    }

}
