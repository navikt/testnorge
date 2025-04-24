package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.mapper.MappingContextUtils;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchIdenterQueryUtils.addIdenterQuery;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonerSearchService {

    private static final String NO_IDENT = "9999999999)";

    private final BestillingQueryService bestillingQueryService;
    private final MapperFacade mapperFacade;
    private final OpenSearchQueryService openSearchQueryService;

    public Mono<SearchResponse> search(SearchRequest searchRequest, List<ElasticTyper> registreRequest) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("registreRequest", registreRequest);

        var request = mapperFacade.map(searchRequest,
                no.nav.testnav.dollysearchservice.dto.SearchRequest.class, context);

        Set<String> identer;
        if (isNull(request.getPersonRequest()) || isBlank(request.getPersonRequest().getIdent())) {

            identer = bestillingQueryService.execRegisterCacheQuery(request);
        } else {

            identer = bestillingQueryService.execRegisterNoCacheQuery(request).stream()
                    .filter(ident -> ident.equals(request.getPersonRequest().getIdent()))
                    .collect(Collectors.toSet());
        }

        request.setIdenter(identer.isEmpty() ? Set.of(NO_IDENT) : identer);

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        addIdenterQuery(query, request.getIdenter());

        return openSearchQueryService.execQuery(request, query)
                .map(response -> mapperFacade.map(response, SearchResponse.class));
    }

    public List<Kategori> getTyper() {

        return Stream.of(ElasticTyper.values())
                .map(entry -> Kategori.builder()
                        .type(entry.name())
                        .beskrivelse(entry.getBeskrivelse())
                        .build())
                .sorted(Comparator.comparing(Kategori::getBeskrivelse))
                .toList();
    }
}
