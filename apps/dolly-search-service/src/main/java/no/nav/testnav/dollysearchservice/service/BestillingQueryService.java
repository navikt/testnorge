package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.config.CachingConfig;
import no.nav.testnav.dollysearchservice.dto.BestillingIdenter;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.utils.FagsystemQuereyUtils;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingQueryService {

    private static final int QUERY_SIZE = 10000;

    @Value("${open.search.index}")
    private String dollyIndex;

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Cacheable(cacheNames = CACHE_REGISTRE, key="#request.registreRequest")
    public Set<String> execRegisterQuery(SearchRequest request) {

        Set<String> identer;
        SearchResponse searchResponse;

        var queryBuilder = QueryBuilders.boolQuery();
        request.getRegistreRequest().stream()
                .map(FagsystemQuereyUtils::getFagsystemQuery)
                .forEach(queryBuilder::must);

        try {
            searchResponse = restHighLevelClient.search(new org.opensearch.action.search.SearchRequest(dollyIndex)
                    .source(new SearchSourceBuilder()
                            .query(queryBuilder)
                            .sort("id")
                            .size(QUERY_SIZE)
                            .timeout(new TimeValue(3, TimeUnit.SECONDS))), RequestOptions.DEFAULT);

            identer = new HashSet<>(getIdenter(searchResponse));

            while (searchResponse.getHits().getHits().length > 0) {

                searchResponse = restHighLevelClient.search(new org.opensearch.action.search.SearchRequest(dollyIndex)
                        .source(new SearchSourceBuilder()
                                .query(queryBuilder)
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
