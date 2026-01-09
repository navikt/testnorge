package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchQueryService {

    private final OpenSearchClient openSearchClient;

    @Value("${open.search.pdl-index}")
    private String pdlIndex;

    public Mono<SearchInternalResponse> execQuery(SearchRequest request, BoolQuery.Builder queryBuilder) {

        var now = System.currentTimeMillis();

        if (isNull(request.getSide())) {
            request.setSide(0);
        }

        if (isNull(request.getAntall())) {
            request.setAntall(10);
        }

        try {
            val personSoekResponse = Mono.just(openSearchClient.search(new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                            .index(pdlIndex)
                            .query(new Query.Builder()
                                    .bool(queryBuilder.build())
                                    .build())
                            .from(request.getSide() * request.getAntall())
                            .size(request.getAntall())
                            .timeout("3s")
                            .build(), JsonNode.class))
                    .map(response -> formatResponse(response, request));

            log.info("Personsøk tok: {} ms", System.currentTimeMillis() - now);

            return personSoekResponse;

        } catch (IOException e) {

            log.error("Feil ved personsøk i OpenSearch", e);
            throw new InternalError("Feil ved personsøk i OpenSearch", e);
        }
    }

    private SearchInternalResponse formatResponse(SearchResponse<JsonNode> response, SearchRequest request) {

        var hits = response.hits();
        if (hits == null) {
            return SearchInternalResponse.builder()
                    .took(Long.toString(response.took()))
                    .totalHits(0L)
                    .antall(0)
                    .side(request.getSide())
                    .seed(request.getSeed())
                    .personer(List.of())
                    .build();
        }
        
        var hitsList = hits.hits();

        return SearchInternalResponse.builder()
                .took(Long.toString(response.took()))
                .totalHits(nonNull(hits.total()) ? hits.total().value() : 0L)
                .antall(nonNull(hitsList) ? hitsList.size() : 0)
                .side(request.getSide())
                .seed(request.getSeed())
                .personer(nonNull(hitsList) ? hitsList.stream()
                        .map(Hit::source)
                        .toList() : List.of())
                .build();
    }
}
