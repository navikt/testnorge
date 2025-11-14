package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.BestillingIdenter;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.utils.FagsystemQueryUtils;
import org.apache.hc.core5.util.TimeValue;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static no.nav.testnav.dollysearchservice.config.CachingConfig.CACHE_REGISTRE;
import static no.nav.testnav.dollysearchservice.utils.FagsystemQueryUtils.addIdentQuery;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingQueryService {

    private static final int QUERY_SIZE = 10000;
    private static final String TESTNORGE_FORMAT = "\\d{2}[8-9]\\d{8}";

    @Value("${open.search.index}")
    private String dollyIndex;

    private final OpenSearchClient opensearchClient;
    private final ObjectMapper objectMapper;

    @Cacheable(cacheNames = CACHE_REGISTRE, key = "{#request.registreRequest, #request.miljoer}")
    public Set<String> execRegisterCacheQuery(SearchRequest request) {

        var queryBuilder = getFagsystemAndMiljoerQuery(request);

        return  execQuery(queryBuilder);
    }

    public Set<String> execRegisterNoCacheQuery(SearchRequest request) {

        var queryBuilder = getFagsystemAndMiljoerQuery(request);
        addIdentQuery(queryBuilder, request.getPersonRequest());

        return execQuery(queryBuilder);
    }

    private static QueryBuilders getFagsystemAndMiljoerQuery(SearchRequest request) {

        var queryBuilder = QueryBuilders.bool();

        request.getRegistreRequest().stream()
                .map(FagsystemQueryUtils::getFagsystemQuery)
                .forEach(queryBuilder::must);

        FagsystemQueryUtils.addMiljoerQuery(queryBuilder, request.getMiljoer());
        return queryBuilder;
    }

    public Set<String> execTestnorgeIdenterQuery() {

        var queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.regexpQuery("identer", TESTNORGE_FORMAT));

        return execQuery(queryBuilder).stream()
                .filter(ident -> ident.matches(TESTNORGE_FORMAT))
                .collect(Collectors.toSet());
    }

    private Set<String> execQuery(QueryBuilder query) {

        var now = System.currentTimeMillis();

        Set<String> identer;
        SearchResponse searchResponse;

        try {
            searchResponse = opensearchClient.search(new org.opensearch.action.search.SearchRequest(dollyIndex)
                    .source(new SearchSourceBuilder()
                            .query(query)
                            .sort("id")
                            .size(QUERY_SIZE)
                            .timeout(new TimeValue(3, TimeUnit.SECONDS))), RequestOptions.DEFAULT);

            identer = new HashSet<>(getIdenter(searchResponse));

            while (searchResponse.getHits().getHits().length > 0) {

                searchResponse = restHighLevelClient.search(new org.opensearch.action.search.SearchRequest(dollyIndex)
                        .source(new SearchSourceBuilder()
                                .query(query)
                                .sort("id")
                                .searchAfter(new Object[]{searchResponse.getHits().getAt(searchResponse.getHits().getHits().length - 1).getId()})
                                .size(QUERY_SIZE)
                                .timeout(new TimeValue(3, TimeUnit.SECONDS))), RequestOptions.DEFAULT);

                identer.addAll(getIdenter(searchResponse));
            }

        } catch (IOException e) {
            log.error("Feil ved henting av identer", e);
            identer = Set.of("99999999999");
        }

        log.info("Uthenting av {} identer tok {} ms", identer.size(), System.currentTimeMillis() - now);

        return identer;
    }

    private Set<String> getIdenter(SearchResponse response) {

        return Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsString)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, BestillingIdenter.class);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved parsing av json", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(BestillingIdenter::getIdenter)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
