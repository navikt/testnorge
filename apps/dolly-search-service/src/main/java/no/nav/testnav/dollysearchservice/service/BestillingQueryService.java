package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.dollysearchservice.dto.BestillingIdenter;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.utils.FagsystemQueryUtils;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.testnav.dollysearchservice.config.CachingConfig.CACHE_REGISTRE;
import static no.nav.testnav.dollysearchservice.utils.FagsystemQueryUtils.addIdentQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.regexpQuery;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingQueryService {

    private static final int QUERY_SIZE = 10000;
    private static final String TESTNORGE_FORMAT = "\\d{2}[8-9]\\d{8}";

    @Value("${open.search.index}")
    private String bestillingIndex;

    private final OpenSearchClient opensearchClient;

    @Cacheable(cacheNames = CACHE_REGISTRE, key = "{#request.registreRequest, #request.miljoer}")
    public Set<String> execRegisterCacheQuery(SearchRequest request) {

        var queryBuilder = getFagsystemAndMiljoerQuery(request);

        return execQuery(queryBuilder);
    }

    public Set<String> execRegisterNoCacheQuery(SearchRequest request) {

        var queryBuilder = getFagsystemAndMiljoerQuery(request);
        addIdentQuery(queryBuilder, request.getPersonRequest());

        return execQuery(queryBuilder);
    }

    private static BoolQuery.Builder getFagsystemAndMiljoerQuery(SearchRequest request) {

        var queryBuilder = QueryBuilders.bool();

        request.getRegistreRequest()
                .forEach(fagsystem -> FagsystemQueryUtils.addFagsystemQuery(queryBuilder, fagsystem));

        FagsystemQueryUtils.addMiljoerQuery(queryBuilder, request.getMiljoer());
        return queryBuilder;
    }

    public Set<String> execTestnorgeIdenterQuery() {

        var queryBuilder = QueryBuilders.bool();

        queryBuilder.must(q -> q.regexp(regexpQuery("identer", TESTNORGE_FORMAT)));

        return execQuery(queryBuilder).stream()
                .filter(ident -> ident.matches(TESTNORGE_FORMAT))
                .collect(Collectors.toSet());
    }

    private Set<String> execQuery(BoolQuery.Builder queryBuilder) {

        var now = System.currentTimeMillis();

        Set<String> identer = new HashSet<>();
        List<String> searchAfter = null;

        val query = queryBuilder.build();

        try {
            while (true) {
                var requestBuilder = new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                        .index(bestillingIndex)
                        .query(q -> q.bool(query))
                        .sort(SortOptions.of(s -> s.field(FieldSort.of(fs -> fs.field("id")))))
                        .size(QUERY_SIZE)
                        .timeout("3s");

                if (searchAfter != null) {
                    requestBuilder.searchAfter(searchAfter);
                }

                var searchResponse = opensearchClient.search(requestBuilder.build(), BestillingIdenter.class);
                var hits = searchResponse.hits().hits();

                if (hits.isEmpty()) {
                    break;
                }

                identer.addAll(getIdenter(searchResponse));
                searchAfter = hits.get(hits.size() - 1).sort();
            }

        } catch (IOException e) {
            log.error("Feil ved henting av identer", e);
            identer = Set.of("99999999999");
        }

        log.info("Uthenting av {} identer tok {} ms", identer.size(), System.currentTimeMillis() - now);

        return identer;
    }

    private Set<String> getIdenter(SearchResponse<BestillingIdenter> response) {

        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(BestillingIdenter::getIdenter)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
