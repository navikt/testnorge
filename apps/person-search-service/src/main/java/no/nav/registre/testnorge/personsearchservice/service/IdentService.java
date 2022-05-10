package no.nav.registre.testnorge.personsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personsearchservice.adapter.IdentSearchAdapter;
import no.nav.registre.testnorge.personsearchservice.domain.IdentSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentSearchAdapter identSearchAdapter;
    private final QueryService queryService;

    public List<IdentdataDTO> getIdenter(String query) {
        var searchRequest = createSearchRequest(getSearchCriteria(query));
        return identSearchAdapter.search(searchRequest);
    }

    private IdentSearch getSearchCriteria(String query) {

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
                .excludeTags(List.of("ARENASYNT"))
                .tag("DOLLY")
                .ident(ident)
                .navn(navn)
                .build();
    }

    private SearchRequest createSearchRequest(IdentSearch search) {
        var queryBuilder = queryService.buildIdentSearchQuery(search);
        return queryService.getSearchRequest(queryBuilder, search.getPage(), search.getPageSize(), search.getTerminateAfter());
    }
}
