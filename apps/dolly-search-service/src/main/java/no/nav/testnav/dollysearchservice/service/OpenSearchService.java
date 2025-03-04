package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private static final String NO_IDENT = "9999999999)";

    private final BestillingQueryService bestillingQueryService;
    private final MapperFacade mapperFacade;
    private final PersonQueryService personQueryService;

    public Mono<SearchResponse> search(SearchRequest searchRequest, List<ElasticTyper> registreRequest) {

        var request = mapperFacade.map(searchRequest,
                no.nav.testnav.dollysearchservice.dto.SearchRequest.class);
        request.setRegistreRequest(registreRequest);

        var identer = bestillingQueryService.execRegisterQuery(request);
        request.setIdenter(identer.isEmpty() ? Set.of(NO_IDENT) : identer);

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);

        return personQueryService.execQuery(request, query)
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
