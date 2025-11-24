package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.PdlProxyConsumer;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchQueryService {

    private final PdlProxyConsumer pdlProxyConsumer;
    private final ObjectMapper objectMapper;

    @Value("${open.search.pdl-index}")
    private String pdlIndex;

    public Mono<SearchInternalResponse> execQuery(SearchRequest request, QueryBuilder query) {

        var now = System.currentTimeMillis();

        if (isNull(request.getSide())) {
            request.setSide(0);
        }

        if (isNull(request.getAntall())) {
            request.setAntall(10);
        }

        var personSoekResponse = Mono.from(pdlProxyConsumer.search(
                        no.nav.testnav.dollysearchservice.dto.SearchRequest.builder()
                                .query(
                                        new org.opensearch.action.search.SearchRequest()
                                                .indices(pdlIndex)
                                                .source(new SearchSourceBuilder()
                                                        .query(query)
                                                        .from(request.getSide() * request.getAntall())
                                                        .size(request.getAntall())
                                                        .timeout(new TimeValue(3, TimeUnit.SECONDS))))
                                .request(request)
                                .build()))
                .map(this::formatResponse);

        log.info("Personsøk tok: {} ms", System.currentTimeMillis() - now);

        return personSoekResponse;
    }

    private SearchInternalResponse formatResponse(no.nav.testnav.dollysearchservice.dto.SearchResponse response) {

        if (isNotBlank(response.getError())) {
            return SearchInternalResponse.builder()
                    .error(response.getError())
                    .build();
        }

        return SearchInternalResponse.builder()
                .took(response.getTook().toString())
                .totalHits(response.getHits().getTotal().getValue())
                .antall(response.getHits().getHits().size())
                .side(response.getRequest().getSide())
                .seed(response.getRequest().getSeed())
                .personer(response.getHits().getHits().stream()
                        .map(no.nav.testnav.dollysearchservice.dto.SearchResponse.SearchHit::get_source)
                        .map(person -> objectMapper.convertValue(person, JsonNode.class))
                        .toList())
                .build();
    }
}
