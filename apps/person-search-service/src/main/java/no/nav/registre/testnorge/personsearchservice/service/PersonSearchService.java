package no.nav.registre.testnorge.personsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.personsearchservice.domain.PdlResponse;
import no.nav.registre.testnorge.personsearchservice.service.utils.QueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.personsearchservice.adapter.PersonSearchAdapter;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;

@Service
@RequiredArgsConstructor
public class PersonSearchService {
    private final PersonSearchAdapter personSearchAdapter;

    @SneakyThrows
    public PersonList search(PersonSearch search){
        var searchRequest = createSearchRequest(search);
        return personSearchAdapter.search(searchRequest);
    }

    @SneakyThrows
    public PdlResponse searchPdlPersoner(PersonSearch search){
        var searchRequest = createSearchRequest(search);
        return personSearchAdapter.searchWithJsonStringResponse(searchRequest);
    }

    private SearchRequest createSearchRequest(PersonSearch search){
        var query = QueryBuilder.buildPersonSearchQuery(search);
        return QueryBuilder.getSearchRequest(query, search.getPage(), search.getPageSize(), search.getTerminateAfter());
    }
}
