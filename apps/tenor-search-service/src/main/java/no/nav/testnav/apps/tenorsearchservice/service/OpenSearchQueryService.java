package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.BestillingIndexSelector;
import no.nav.testnav.apps.tenorsearchservice.domain.SearchResponse;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchQueryService {

    private final OpenSearchClient openSearchClient;

    @Value("${open.search.index}")
    private String index;

    @Value("${open.search.index-dev}")
    private String indexDev;

    public Mono<SearchResponse> execQuery(List<String> identer, BestillingIndexSelector selector, String bankIdOrgnr) {

        try {
            var now = System.currentTimeMillis();

            var queryBuilder = new BoolQuery.Builder();
            identerQuery(queryBuilder, identer);
            bankIdOrgnrQuery(queryBuilder, bankIdOrgnr);

            var response = openSearchClient.search(new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                    .index(selector == BestillingIndexSelector.REGULAR ? index : indexDev)
                    .query(Query.builder()
                            .bool(queryBuilder.build())
                            .build())
                    .timeout("3s")
                    .build(), JsonNode.class);

            log.info("Personsøk tok: {} ms", System.currentTimeMillis() - now);

            return Mono.just(formatResponse(response));

        } catch (IOException | RuntimeException e) {
            log.error("Feil ved personsøk i OpenSearch", e);
            return Mono.error(new ResponseStatusException(INTERNAL_SERVER_ERROR, "Feil ved personsøk i OpenSearch", e));
        }
    }

    private SearchResponse formatResponse(org.opensearch.client.opensearch.core.SearchResponse<JsonNode> response) {

        if (isNull(response.hits())) {
            return SearchResponse.builder()
                    .build();
        }

        return SearchResponse.builder()
                .identer(response.hits().hits().stream()
                        .map(Hit::source)
                        .filter(Objects::nonNull)
                        .map(jsonNode -> jsonNode.path("identer"))
                        .flatMap(jsonNode -> StreamSupport
                                .stream(jsonNode.spliterator(), false))
                        .map(Object::toString)
                        .collect(Collectors.toSet()))
                .build();
    }

    private void identerQuery(BoolQuery.Builder queryBuilder, List<String> identer) {

        if (nonNull(identer) && !identer.isEmpty()) {
            queryBuilder
                    .must(q -> q
                            .terms(t -> t
                                    .field("identer")
                                    .terms(r -> r.value(identer.stream().map(FieldValue::of).toList()))));
        }
    }

    private void bankIdOrgnrQuery(BoolQuery.Builder queryBuilder, String bankIdOrgnr) {

        if (isNotBlank(bankIdOrgnr)) {
            queryBuilder
                    .must(q -> q
                            .match(t -> t
                                    .field("brukerType")
                                    .query(FieldValue.of("BANKID"))))
                    .must(q -> q
                            .match(t -> t
                                    .field("orgnr")
                                    .query(FieldValue.of(bankIdOrgnr))));
        }
    }
}
