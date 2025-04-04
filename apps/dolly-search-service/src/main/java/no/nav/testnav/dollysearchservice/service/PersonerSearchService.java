package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchIdenterQueryUtils.addIdenterQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonerSearchService {

    private static final String NO_IDENT = "9999999999)";

    private final BestillingQueryService bestillingQueryService;
    private final MapperFacade mapperFacade;
    private final OpenSearchQueryService openSearchQueryService;

    public Mono<SearchResponse> search(SearchRequest searchRequest, List<ElasticTyper> registreRequest) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("registreRequest", registreRequest);

        var request = mapperFacade.map(searchRequest,
                no.nav.testnav.dollysearchservice.dto.SearchRequest.class, context);

        var identer = Optional.ofNullable(searchRequest.getPersonRequest())
                .filter(personRequest -> isNotBlank(personRequest.getIdent()))
                .map(personrequest -> Set.of(personrequest.getIdent()))
                .orElse(bestillingQueryService.execRegisterQuery(request));

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
