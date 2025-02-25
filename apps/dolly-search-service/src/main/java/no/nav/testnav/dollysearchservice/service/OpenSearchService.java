package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.consumer.OpenSearchConsumer;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchConsumer openSearchConsumer;
    private final MapperFacade mapperFacade;
    private final PersonQueryService personQueryService;

    public Mono<SearchResponse> search(SearchRequest searchRequest) {

        var request = mapperFacade.map(searchRequest, no.nav.testnav.dollysearchservice.dto.SearchRequest.class);
        var query = OpenSearchQueryBuilder.buildSearchQuery(request);

        return personQueryService.execQuery(request, query)
                .map(response -> mapperFacade.map(response, SearchResponse.class));
    }
}
