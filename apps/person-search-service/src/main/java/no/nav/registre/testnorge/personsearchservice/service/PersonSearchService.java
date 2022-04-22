package no.nav.registre.testnorge.personsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.personsearchservice.domain.PdlResponse;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.personsearchservice.adapter.PersonSearchAdapter;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonSearchService {
    private final PersonSearchAdapter personSearchAdapter;

    @SneakyThrows
    public PersonList search(PersonSearch search){
        return personSearchAdapter.search(search);
    }

    @SneakyThrows
    public PdlResponse searchPdlPersoner(PersonSearch search){
        return personSearchAdapter.searchWithJsonStringResponse(search);
    }
}
