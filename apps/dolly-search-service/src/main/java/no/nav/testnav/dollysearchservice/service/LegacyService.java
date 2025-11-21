package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.legacy.PersonDTO;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.legacy.PersonSearch;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.termsQuery;

@Service
@RequiredArgsConstructor
public class LegacyService {

    private final OpenSearchQueryService openSearchQueryService;
    private final MapperFacade mapperFacade;

    public Flux<PersonDTO> searchPersoner(PersonSearch personSearch) {

        var personRequest = SearchRequest.builder()
                .seed(personSearch.getRandomSeed())
                .side(personSearch.getPage())
                .antall(personSearch.getPageSize())
                .personRequest(mapperFacade.map(personSearch, PersonRequest.class))
                .build();

        var query = OpenSearchQueryBuilder.buildSearchQuery(personRequest);
        query.mustNot(q -> q.terms(termsQuery("tags", Set.of("DOLLY", "ARENASYNT"))));
        query.must(q -> q.match(matchQuery("tags", "TESTNORGE")));

        return openSearchQueryService.execQuery(personRequest, query)
                .map(this::formatResponse)
                .flatMapMany(Flux::fromIterable);
    }

    private List<PersonDTO> formatResponse(SearchInternalResponse response) {

        return response.getPersoner().stream()
                .map(person -> mapperFacade.map(person, PersonDTO.class))
                .toList();
    }
}
