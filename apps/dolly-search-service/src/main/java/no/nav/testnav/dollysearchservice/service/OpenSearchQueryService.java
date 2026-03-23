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

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchQueryService {

    private final OpenSearchClient openSearchClient;

    @Value("${open.search.pdl-index}")
    private String pdlIndex;

    public SearchInternalResponse execQuery(SearchRequest request, BoolQuery.Builder queryBuilder) {

        if (request.getSide() == null) {
            request.setSide(0);
        }

        if (request.getAntall() == null) {
            request.setAntall(10);
        }

        try {
            var now = System.currentTimeMillis();

            val response = openSearchClient.search(new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                    .index(pdlIndex)
                    .query(new Query.Builder()
                            .bool(queryBuilder.build())
                            .build())
                    .from(request.getSide() * request.getAntall())
                    .size(request.getAntall())
                    .timeout("3s")
                    .build(), JsonNode.class);

            log.info("Personsøk tok: {} ms", System.currentTimeMillis() - now);

            return formatResponse(response, request);

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
