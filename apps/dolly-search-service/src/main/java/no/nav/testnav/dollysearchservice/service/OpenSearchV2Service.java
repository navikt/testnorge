package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v2.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v2.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v2.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchV2Service {

    private final BestillingQueryService bestillingQueryService;
    private final MapperFacade mapperFacade;
    private final PersonQueryService personQueryService;

    public Mono<SearchResponse> search(SearchRequest searchRequest, List<ElasticTyper> registreRequest) {

        var request = mapperFacade.map(searchRequest,
                no.nav.testnav.dollysearchservice.dto.SearchRequest.class);
        request.setRegistreRequest(registreRequest);

        var identer = bestillingQueryService.execRegisterQuery(request);
        request.setIdenter(identer.isEmpty() ? Set.of("99999999999") : identer);

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);

        return personQueryService.execQuery(request,query)
                .map(response -> mapperFacade.map(response, SearchResponse.class));
    }
}
