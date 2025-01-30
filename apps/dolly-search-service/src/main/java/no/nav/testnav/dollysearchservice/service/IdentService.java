package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;
import no.nav.testnav.dollysearchservice.model.SearchResponse;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticBestilling;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addDollyIdentifier;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final RestHighLevelClient restHighLevelClient;

    public SearchResponse getIdenter(ElasticTyper[] typer) {

        var queryBuilder = OpenSearchQueryBuilder.buildTyperQuery(typer);
        return execQuery(queryBuilder);
    }

    private SearchResponse execQuery(BoolQueryBuilder query) {

        var searchRequest = new org.opensearch.action.search.SearchRequest(index);
        searchRequest.source(new SearchSourceBuilder().query(query)
                .size(50));

        try {
            var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return getIdenter(response);

        } catch (IOException e) {
            log.error("OpenSearch feil ved utføring av søk: {}", e.getMessage(), e);
            return SearchResponse.builder()
                    .error(e.getLocalizedMessage())
                    .build();
        }
    }
}
