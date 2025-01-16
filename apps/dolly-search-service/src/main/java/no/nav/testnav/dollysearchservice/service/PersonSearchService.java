package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.ElasticSearchConsumer;
import no.nav.testnav.dollysearchservice.domain.Person;
import no.nav.testnav.dollysearchservice.service.utils.QueryBuilder;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonSearchService {

    private final ElasticSearchConsumer elasticSearchConsumer;

//    @SneakyThrows
//    public Mono<SearchResponse> search(PersonSearch search) {
//        var searchRequest = createSearchRequest(search);
//        return elasticSearchConsumer.search(searchRequest)
//    }

    public Flux<String> searchPdlPersoner(PersonSearch search) {
        var searchRequest = createSearchRequest(search);
        return elasticSearchConsumer.searchWithJsonResponse(searchRequest);
    }

    private SearchRequest createSearchRequest(PersonSearch search) {
        var query = QueryBuilder.buildPersonSearchQuery(search);
        return QueryBuilder.getSearchRequest(query, search.getPage(), search.getPageSize(), search.getTerminateAfter());
    }
}
