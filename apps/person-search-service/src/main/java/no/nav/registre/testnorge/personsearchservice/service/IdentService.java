package no.nav.registre.testnorge.personsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.testnorge.personsearchservice.consumer.ElasticSearchConsumer;
import no.nav.registre.testnorge.personsearchservice.domain.IdentSearch;
import no.nav.registre.testnorge.personsearchservice.model.SearchResponse;
import no.nav.registre.testnorge.personsearchservice.service.utils.QueryBuilder;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final ElasticSearchConsumer identSearchConsumer;
    private final MapperFacade mapperFacade;

    @SneakyThrows
    public Flux<IdentdataDTO> getIdenter(String query) {

        var searchRequest = createSearchRequest(getSearchCriteria(query));
        return identSearchConsumer.search(searchRequest)
                .map(SearchResponse.SearchHit::get_source)
                .map(response -> mapperFacade.map(response, IdentdataDTO.class));
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

        var query = QueryBuilder.buildIdentSearchQuery(search);
        return QueryBuilder.getSearchRequest(query, search.getPage(), search.getPageSize(), search.getTerminateAfter());
    }
}
