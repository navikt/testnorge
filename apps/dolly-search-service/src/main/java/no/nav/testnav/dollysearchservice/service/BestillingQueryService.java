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
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.dollysearchservice.config.CachingConfig.CACHE_TESTNORGE_IDENTER;
import static no.nav.testnav.dollysearchservice.config.CachingConfig.CACHE_REGISTRE;
import static no.nav.testnav.dollysearchservice.utils.FagsystemQueryUtils.addIdentQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.regexpQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingQueryService {

    private static final int QUERY_SIZE = 1000;
    private static final String TESTNORGE_FORMAT = "\\d{2}[8-9]\\d{8}";
    private static final String OPENSEARCH_ERROR_FALLBACK_IDENT = "99999999999";

    private final OpenSearchClient opensearchClient;

    @Value("${open.search.index}")
    private String bestillingIndex;

    @Cacheable(cacheNames = CACHE_REGISTRE, key = "{#request.registreRequest, #request.miljoer, #request.orgnr}")
    public Mono<Set<String>> execRegisterCacheQuery(SearchRequest request) {

        var queryBuilder = getFagsystemAndMiljoerQuery(request);

        return execQuery(queryBuilder);
    }

    public Mono<Set<String>> execRegisterNoCacheQuery(SearchRequest request) {

        var queryBuilder = getFagsystemAndMiljoerQuery(request);
        addIdentQuery(queryBuilder, request.getPersonRequest());

        return execQuery(queryBuilder);
    }

    @Cacheable(cacheNames = CACHE_TESTNORGE_IDENTER, key = "{#orgnr}")
    public Mono<Set<String>> execTestnorgeIdenterCacheQuery(String orgnr) {

        var queryBuilder = QueryBuilders.bool();

        if (isNotBlank(orgnr)) {
            queryBuilder.must(q -> q.match(matchQuery("brukerType", "BANKID")));
            queryBuilder.must(q -> q.match(matchQuery("orgnr", orgnr)));
        }

        return execTestnorgeQuery(queryBuilder);
    }

    public Mono<Set<String>> execTestnorgeIdenterNoCacheQuery() {

        var queryBuilder = QueryBuilders.bool();

        return execTestnorgeQuery(queryBuilder);
    }

    private Mono<Set<String>> execTestnorgeQuery(BoolQuery.Builder queryBuilder) {

        queryBuilder.must(q -> q.regexp(regexpQuery("identer", TESTNORGE_FORMAT)));

        return execQuery(queryBuilder)
                .flatMapMany(Flux::fromIterable)
                .filter(ident -> !ident.equals(OPENSEARCH_ERROR_FALLBACK_IDENT))
                .filter(ident -> ident.matches(TESTNORGE_FORMAT))
                .collect(Collectors.toSet());
    }

    private Mono<Set<String>> execQuery(BoolQuery.Builder queryBuilder) {

        return Mono.fromCallable(() -> {
                    var now = System.currentTimeMillis();

                    val query = new Query.Builder()
                            .bool(queryBuilder.build())
                            .build();

                    Set<String> identer = new HashSet<>();

                    var searchResponse = opensearchClient.search(new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                            .index(bestillingIndex)
                            .query(query)
                            .sort(SortOptions.of(s -> s.field(FieldSort.of(fs -> fs.field("id").unmappedType(FieldType.Long)))))
                            .size(QUERY_SIZE)
                            .timeout("3s")
                            .build(), BestillingIdenter.class);

                    while (hasHits(searchResponse)) {

                        identer.addAll(getIdenter(searchResponse));

                        var lastHit = searchResponse.hits().hits().getLast();
                        if (isNull(lastHit)) {
                            break;
                        }

                        searchResponse = opensearchClient.search(new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                                .index(bestillingIndex)
                                .query(query)
                                .sort(SortOptions.of(s -> s.field(FieldSort.of(fs -> fs.field("id").unmappedType(FieldType.Long)))))
                                .size(QUERY_SIZE)
                                .searchAfter(lastHit.sort())
                                .timeout("3s")
                                .build(), BestillingIdenter.class);
                    }
                    log.info("Uthenting av {} identer tok {} ms", identer.size(), System.currentTimeMillis() - now);

                    return identer;
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    log.error("Feil ved henting av identer", e);
                    return Mono.just(Set.of(OPENSEARCH_ERROR_FALLBACK_IDENT));
                });
    }

    private Set<String> getIdenter(SearchResponse<BestillingIdenter> response) {

        var hits = response.hits();
        if (isNull(hits)) {
            return Set.of();
        }

        return hits.hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(BestillingIdenter::getIdenter)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static boolean hasHits(SearchResponse<BestillingIdenter> response) {

        return nonNull(response.hits())
               && !response.hits().hits().isEmpty();
    }

    private static BoolQuery.Builder getFagsystemAndMiljoerQuery(SearchRequest request) {

        var queryBuilder = QueryBuilders.bool();

        var registreRequest = request.getRegistreRequest();
        if (registreRequest != null && !registreRequest.isEmpty()) {
            registreRequest.forEach(fagsystem -> FagsystemQueryUtils.addFagsystemQuery(queryBuilder, fagsystem));
        }

        FagsystemQueryUtils.addMiljoerQuery(queryBuilder, request.getMiljoer());
        FagsystemQueryUtils.addOrgnrQuery(queryBuilder, request);
        return queryBuilder;
    }
}
