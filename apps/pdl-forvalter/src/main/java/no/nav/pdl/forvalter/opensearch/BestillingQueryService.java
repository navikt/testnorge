package no.nav.pdl.forvalter.opensearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.pdl.forvalter.dto.BestillingIdenter;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.config.CachingConfig.CACHE_IDENTER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingQueryService {

    private static final int QUERY_SIZE = 1000;
    private static final String OPENSEARCH_ERROR_FALLBACK_IDENT = "99999999999";

    private final OpenSearchClient opensearchClient;

    @Value("${open.search.index}")
    private String bestillingIndex;

    @Cacheable(cacheNames = CACHE_IDENTER, key = "#orgnr")
    public Mono<Set<String>> execIdenterCacheQuery(String orgnr) {

        var queryBuilder = QueryBuilders.bool();

        if (isNotBlank(orgnr)) {
            queryBuilder.must(q -> q.match(matchQuery("brukerType","BANKID")));
            queryBuilder.must(q -> q.match(matchQuery("orgnr", orgnr)));
        }

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
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static boolean hasHits(SearchResponse<BestillingIdenter> response) {

        return nonNull(response.hits())
               && !response.hits().hits().isEmpty();
    }

    public static MatchQuery matchQuery(String field, Object value) {

        return QueryBuilders.match()
                .field(field)
                .query(FieldValue.of(value.toString()))
                .build();
    }
}
