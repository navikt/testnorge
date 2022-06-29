package no.nav.registre.testnorge.personsearchservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.personsearchservice.consumer.ElasticSearchConsumer;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.service.utils.QueryBuilder;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonSearchService {

    private final ElasticSearchConsumer elasticSearchConsumer;

    @SneakyThrows
    public Flux<Person> search(PersonSearch search) {
        var searchRequest = createSearchRequest(search);
        return elasticSearchConsumer.search(searchRequest)
                .map(Person::new);
    }

    public Flux<String> searchPdlPersoner(PersonSearch search) {
        var searchRequest = createSearchRequest(search);
        return elasticSearchConsumer.searchWithJsonResponse(searchRequest);
    }

    private SearchRequest createSearchRequest(PersonSearch search) {
        var query = QueryBuilder.buildPersonSearchQuery(search);
        return QueryBuilder.getSearchRequest(query, search.getPage(), search.getPageSize(), search.getTerminateAfter());
    }
}
