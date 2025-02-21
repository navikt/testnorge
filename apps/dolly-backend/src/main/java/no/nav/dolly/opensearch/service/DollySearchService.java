package no.nav.dolly.opensearch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.service.OpenSearchQueryBuilder;
import no.nav.dolly.opensearch.DollySearchServiceConsumer;
import no.nav.dolly.opensearch.dto.SearchRequest;
import no.nav.dolly.opensearch.dto.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollySearchService {

    private static final Random SEED = new SecureRandom();

    private final RestHighLevelClient restHighLevelClient;
    private final DollySearchServiceConsumer dollySearchServiceConsumer;
    private final MapperFacade mapperFacade;

    @Value("${open.search.index}")
    private String index;

    public Mono<SearchResponse> search(List<ElasticTyper> registre, SearchRequest request) {

        var personRequest = mapperFacade.map(request, no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest.class);
        var response = new SearchResponse();

        var registreResponse = execRegistreQuery(registre, request);
        response.setRegistreSearchResponse(registreResponse);
        personRequest.setIdenter(new HashSet<>(!registreResponse.getIdenter().isEmpty() ?
                registreResponse.getIdenter() : List.of("99999999999")));

        return dollySearchServiceConsumer.doPersonSearch(personRequest)
                .map(personResultat -> {
                    response.setDollySearchResponse(personResultat);
                    response.getRegistreSearchResponse().setIdenter(null);
                    return response;
                });
    }

    private SearchResponse.RegistreResponseStatus execRegistreQuery(List<ElasticTyper> registre, SearchRequest request) {

        var side = isNull(request.getPagineringBestillingRequest().getSide()) ?
                1 : request.getPagineringBestillingRequest().getSide();
        var antall = isNull(request.getPagineringBestillingRequest().getAntall()) ?
                500 : request.getPagineringBestillingRequest().getAntall();
        var seed = isNull(request.getPagineringBestillingRequest().getSeed()) ?
                SEED.nextInt() : request.getPagineringBestillingRequest().getSeed();

        var query = buildTyperQuery(registre, seed);
        var searchRequest = new org.opensearch.action.search.SearchRequest(index);
        searchRequest
                .source(new SearchSourceBuilder().query(query)
                        .size(antall)
                        .from(side));

        try {
            var registerResultat = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            var registreResponse = getIdenter(registerResultat);
            registreResponse.setRegistre(registre);
            registreResponse.setSide(side);
            registreResponse.setAntall(antall);
            registreResponse.setSeed(seed);

            return registreResponse;

        } catch (IOException e) {
            log.error("OpenSearch feil ved utføring av søk: {}", e.getMessage(), e);
            return SearchResponse.RegistreResponseStatus.builder()
                    .error(e.getLocalizedMessage())
                    .build();
        }
    }

    private static SearchResponse.RegistreResponseStatus getIdenter(org.opensearch.action.search.SearchResponse response) {

        var identer = Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .map(map -> (List<String>) map.get("identer"))
                .flatMap(Collection::stream)
                .distinct()
                .limit(1000)
                .toList();
        return SearchResponse.RegistreResponseStatus.builder()
                .identer(identer)
                .totalHitsBestillinger(getTotalHits(response.getHits()))
                .took(response.getTook().getStringRep())
                .antallIdenter(identer.size())
                .score(response.getHits().getMaxScore())
                .build();
    }

    private static Long getTotalHits(SearchHits searchHits) {

        return nonNull(searchHits) && nonNull(searchHits.getTotalHits()) ?
                searchHits.getTotalHits().value : null;
    }

    private static BoolQueryBuilder buildTyperQuery(List<ElasticTyper> typer, Integer seed) {

        var queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.functionScoreQuery(
                        new RandomScoreFunctionBuilder().seed(seed)));

        if (nonNull(typer)) {
            typer.stream()
                    .map(OpenSearchQueryBuilder::getFagsystemQuery)
                    .forEach(queryBuilder::must);
        }

        return queryBuilder;
    }
}
