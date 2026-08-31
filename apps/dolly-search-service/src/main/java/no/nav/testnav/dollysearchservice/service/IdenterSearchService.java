package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JsonNode;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPdlIdenterQueryUtils.buildTestnorgeIdentSearchQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdenterSearchService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final BestillingQueryService bestillingQueryService;
    private final PdlPersonQueryService pdlPersonQueryService;
    private final MapperFacade mapperFacade;

    public Flux<IdentdataDTO> getIdenter(String fragment, int side, int antall, Integer seed, String orgnr) {

        var resolvedSeed = isNull(seed) ? RANDOM.nextInt() : seed;
        return bestillingQueryService.execTestnorgeIdenterCacheQuery(orgnr)
                .flatMapMany(identer -> {
                    var query = buildTestnorgeIdentSearchQuery(getSearchCriteria(fragment, identer, resolvedSeed));
                    var request = SearchRequest.builder()
                            .side(side)
                            .antall(antall)
                            .seed(resolvedSeed)
                            .build();

                    return pdlPersonQueryService.execQuery(request, query)
                            .map(SearchInternalResponse::getPersoner)
                            .flatMapMany(Flux::fromIterable)
                            .map(this::formatResponse);
                });
    }

    private IdentSearch getSearchCriteria(String query, Set<String> identer, int seed) {

        var ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst()
                .orElse(null);

        var navn = Stream.of(query.split(" "))
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

        return IdentSearch.builder()
                .seed(seed)
                .tags(List.of("DOLLY", "TESTNORGE"))
                .ident(ident)
                .navn(navn)
                .identer(identer)
                .build();
    }

    private IdentdataDTO formatResponse(JsonNode jsonNode) {

        return mapperFacade.map(jsonNode, IdentdataDTO.class);
    }
}
