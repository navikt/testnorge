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

import java.util.List;

import no.nav.registre.testnorge.personsearchservice.adapter.PersonSearchAdapter;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.Search;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonSearchService {
    private final PersonSearchAdapter personSearchAdapter;


    @SneakyThrows
    public List<Person> search(Search search){
        return personSearchAdapter.search(search);
    }

}
