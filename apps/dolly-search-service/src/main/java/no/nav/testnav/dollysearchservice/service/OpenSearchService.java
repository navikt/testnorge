package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.OpenSearchConsumer;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchConsumer openSearchConsumer;
    private final ObjectMapper objectMapper;

    public Mono<SearchResponse> search(SearchRequest request) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        return execQuery(request, query);
    }

    private Mono<SearchResponse> execQuery(SearchRequest request, BoolQueryBuilder query) {

        if (isNull(request.getSide())) {
            request.setSide(1);
        }

        if (isNull(request.getAntall())) {
            request.setAntall(10);
        }

        return Mono.from(openSearchConsumer.search(
                        no.nav.testnav.dollysearchservice.dto.SearchRequest.builder()
                                .query(
                                        new org.opensearch.action.search.SearchRequest()
                                                .indices("pdl-sok")
                                                .source(new SearchSourceBuilder()
                                                        .query(query)
                                                        .from(request.getSide() * request.getAntall())
                                                        .size(request.getAntall())
                                                        .timeout(new TimeValue(3, TimeUnit.SECONDS))))
                                .request(request)
                                .build()))
                .map(this::formatResponse);
    }

    private SearchResponse formatResponse(no.nav.testnav.dollysearchservice.dto.SearchResponse response) {

        if (isNotBlank(response.getError())) {
            return SearchResponse.builder()
                    .error(response.getError())
                    .build();
        }

        return SearchResponse.builder()
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
