package no.nav.testnav.dollysearchservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.ElasticParamsConsumer;
import no.nav.testnav.dollysearchservice.consumer.ElasticSearchConsumer;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.dto.SearchResponse;
import no.nav.testnav.dollysearchservice.model.HentIdenterModel;
import no.nav.testnav.dollysearchservice.model.IdenterModel;
import no.nav.testnav.dollysearchservice.model.Response;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticBestilling;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final ElasticParamsConsumer elasticParamsConsumer;
//    private final BestillingElasticRepository bestillingElasticRepository;
    private final ElasticSearchConsumer elasticSearchConsumer;
    private final ObjectMapper objectMapper;

    @Value("${open.search.index}")
    private String index;

    public Mono<SearchResponse> getTyper(ElasticTyper[] typer) {

        var query = OpenSearchQueryBuilder.buildTyperQuery(typer);
        return execQuery(query);
    }

    public Mono<SearchResponse> search(SearchRequest request) {

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        return execQuery(query);
    }

    public List<ElasticBestilling> search(String ident) {

//        return bestillingElasticRepository.getAllByIdenterIn(List.of(ident));
        return List.of();
    }

    public List<Kategori> getTyper() {

        return Stream.of(ElasticTyper.values())
                .map(entry -> Kategori.builder()
                        .type(entry.name())
                        .beskrivelse(entry.getBeskrivelse())
                        .build())
                .sorted(Comparator.comparing(Kategori::getBeskrivelse))
                .toList();
    }

    private Mono<SearchResponse> execQuery(BoolQueryBuilder query) {

            return Mono.from(elasticSearchConsumer.search(new org.opensearch.action.search.SearchRequest()
                            .indices("pdl-sok")
                            .source(new SearchSourceBuilder()
                                    .query(query)
                                    .size(1000)
                                    .timeout(new TimeValue(3, TimeUnit.SECONDS))))
                    .map(this::getIdenter));
    }


    private SearchResponse getIdenter(no.nav.testnav.dollysearchservice.model.SearchResponse response) {

        return SearchResponse.builder()
                .took(response.getTook().toString())
                .totalHits(response.getHits().getTotal().getValue())
                .score(response.getHits().getMaxScore())
                .identer(response.getHits().getHits().stream()
                        .map(no.nav.testnav.dollysearchservice.model.SearchResponse.SearchHit::get_source)
                        .map(result -> objectMapper.convertValue(result, Response.class))
                        .map(Response::getHentIdenter)
                        .map(HentIdenterModel::getIdenter)
                        .flatMap(Collection::stream)
                        .filter(IdenterModel::isFolkeregisterident)
                        .map(IdenterModel::getIdent)
                        .toList())
                .build();
    }

    @SuppressWarnings("java:S2259")
    private static Long getTotalHits(SearchHits searchHits) {

        return nonNull(searchHits) && nonNull(searchHits.getTotalHits()) ?
                searchHits.getTotalHits().value : null;
    }
}
