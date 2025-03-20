package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchIdenterQueryUtils.buildTestnorgeIdentSearchQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdenterSearchService {

    private final BestillingQueryService bestillingQueryService;
    private final OpenSearchQueryService personQueryService;
    private final MapperFacade mapperFacade;

    @SneakyThrows
    public Flux<IdentdataDTO> getIdenter(String fragment) {

        var identer = bestillingQueryService.execTestnorgeIdenterQuery();
        var query = buildTestnorgeIdentSearchQuery(getSearchCriteria(fragment, identer));

        return personQueryService.execQuery(new SearchRequest(), query)
                .map(this::formatResponse)
                .flatMapMany(Flux::fromIterable);
    }

    private IdentSearch getSearchCriteria(String query, Set<String> identer) {

        var ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst()
                .orElse(null);

        var navn = Stream.of(query.split(" "))
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

        return IdentSearch.builder()
                .page(1)
                .pageSize(10)
                .tags(List.of("DOLLY", "TESTNORGE"))
                .ident(ident)
                .navn(navn)
                .identer(identer)
                .build();
    }

    private List<IdentdataDTO> formatResponse(SearchInternalResponse response) {

        return response.getPersoner().stream()
                .map(person -> mapperFacade.map(person, IdentdataDTO.class))
                .toList();
    }
}
