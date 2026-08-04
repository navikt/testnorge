package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchQueryService {

    private final OpenSearchClient openSearchClient;
    private final JsonMapper jsonMapper;

    @Value("${open.search.pdl-index}")
    private String pdlIndex;

    public SearchInternalResponse execQuery(SearchRequest request, FunctionScoreQuery.Builder queryBuilder) {

        if (isNull(request.getSide())) {
            request.setSide(0);
        }

        if (isNull(request.getAntall())) {
            request.setAntall(10);
        }

        try {
            var now = System.currentTimeMillis();

            var response = openSearchClient.search(new org.opensearch.client.opensearch.core.SearchRequest.Builder()
                    .index(pdlIndex)
                    .query(Query.builder()
                            .functionScore(queryBuilder.build())
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
        if (isNull(hits)) {
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
                .antall(hitsList.size())
                .side(request.getSide())
                .seed(request.getSeed())
                .personer(hitsList.stream()
                        .map(Hit::source)
                        .map(node -> jsonMapper.readTree(nonNull(node) ? node.toString() : null ))
                        .toList())
                .build();
    }
}
