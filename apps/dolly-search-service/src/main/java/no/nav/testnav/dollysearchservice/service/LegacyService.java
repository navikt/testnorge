package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.dto.dollysearchservice.v1.PersonRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonDTO;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonSearch;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JsonNode;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LegacyService {

    private final PdlPersonQueryService pdlPersonQueryService;
    private final MapperFacade mapperFacade;

    public Flux<PersonDTO> searchPersoner(PersonSearch personSearch) {

        var personRequest = SearchRequest.builder()
                .seed(personSearch.getRandomSeed())
                .side(personSearch.getPage())
                .antall(personSearch.getPageSize())
                .personRequest(mapperFacade.map(personSearch, PersonRequest.class))
                .mustHaveTags(Set.of("TESTNORGE"))
                .mustNotHaveTags(Set.of("DOLLY", "ARENASYNT"))
                .build();

        var query = OpenSearchQueryBuilder.buildSearchQuery(personRequest);

        return pdlPersonQueryService.execQuery(personRequest, query)
                .map(SearchInternalResponse::getPersoner)
                .flatMapMany(Flux::fromIterable)
                .map(this::formatResponse);
    }

    private PersonDTO formatResponse(JsonNode jsonNode) {

        return mapperFacade.map(jsonNode, PersonDTO.class);
    }
}
