package no.nav.testnav.dollysearchservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchIdenterQueryUtils.buildTestnorgeIdentSearchQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdenterSearchService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TESTNORGE_IDENTER_CACHE_KEY = 0;

    private final Cache<Integer, Set<String>> testnorgeIdenterCache = Caffeine.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final BestillingQueryService bestillingQueryService;
    private final OpenSearchQueryService personQueryService;
    private final MapperFacade mapperFacade;

    public List<IdentdataDTO> getIdenter(String fragment, int side, int antall, Integer seed) {

        var resolvedSeed = isNull(seed) ? RANDOM.nextInt() : seed;
        var identer = testnorgeIdenterCache.get(TESTNORGE_IDENTER_CACHE_KEY,
                ignored -> bestillingQueryService.execTestnorgeIdenterQuery());
        var query = buildTestnorgeIdentSearchQuery(getSearchCriteria(fragment, identer, resolvedSeed));
        var request = SearchRequest.builder()
                .side(side)
                .antall(antall)
                .seed(resolvedSeed)
                .build();

        var response = personQueryService.execQuery(request, query);
        return formatResponse(response);
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

    private List<IdentdataDTO> formatResponse(SearchInternalResponse response) {

        return response.getPersoner().stream()
                .map(person -> mapperFacade.map(person, IdentdataDTO.class))
                .toList();
    }
}
