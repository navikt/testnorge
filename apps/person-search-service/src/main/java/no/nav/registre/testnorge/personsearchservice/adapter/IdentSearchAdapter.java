package no.nav.registre.testnorge.personsearchservice.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.domain.IdentSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentSearchAdapter {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";

    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;
    private final RestHighLevelClient client;

    private static void addNameQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {

        Optional.ofNullable(search.getNavn())
                .ifPresent(values -> {
                    if (values.size() == 1) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.navn",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.regexpQuery(PERSON_FORNAVN, ".*" + values.get(0) + ".*"))
                                        .should(QueryBuilders.regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(0) + ".*"))
                                        .minimumShouldMatch(1),
                                ScoreMode.Avg));

                    } else if (values.size() > 1) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.navn",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.boolQuery()
                                                .must(QueryBuilders.regexpQuery(PERSON_FORNAVN, ".*" + values.get(0) + ".*"))
                                                .must(QueryBuilders.regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(1) + ".*")))
                                        .should(QueryBuilders.boolQuery()
                                                .must(QueryBuilders.regexpQuery(PERSON_FORNAVN, ".*" + values.get(1) + ".*"))
                                                .must(QueryBuilders.regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(0) + ".*")))
                                        .minimumShouldMatch(1),
                                ScoreMode.Avg));
                    }
                });
    }

    private static void addIdentQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {

        Optional.ofNullable(search.getIdent())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.folkeregisteridentifikator",
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.regexpQuery("hentPerson.folkeregisteridentifikator.identifikasjonsnummer", ".*" + value + ".*"))
                                ,
                                ScoreMode.Avg));
                    }
                });
    }

    private <T> List<T> convert(SearchHit[] hits, Class<T> clazz) {
        return Arrays.stream(hits).map(SearchHit::getSourceAsString).map(json -> {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Feil med konvertering fra json: " + json, e);
            }
        }).toList();
    }

    @SneakyThrows
    public List<IdentdataDTO> search(IdentSearch search) {

        var searchResponse = getSearchResponse(search);

        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        var response = convert(searchResponse.getHits().getHits(), Response.class);
        return mapperFacade.mapAsList(response, IdentdataDTO.class);
    }

    @SneakyThrows
    private SearchResponse getSearchResponse(IdentSearch search) {
        var queryBuilder = QueryBuilders.boolQuery();

        buildQuery(queryBuilder, search);

        var searchSourceBuilder = getSearchSourceBuilder(queryBuilder, search);
        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");
        searchRequest.source(searchSourceBuilder);

        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    private void buildQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {

        addTagsQueries(queryBuilder, search);
        addIdentQuery(queryBuilder, search);
        addNameQuery(queryBuilder, search);
    }

    private SearchSourceBuilder getSearchSourceBuilder(BoolQueryBuilder queryBuilder, IdentSearch search) {
        var searchSourceBuilder = new SearchSourceBuilder();
        int page = search.getPage();
        int pageSize = search.getPageSize();
        searchSourceBuilder.from((page - 1) * pageSize);
        searchSourceBuilder.timeout(new TimeValue(1, TimeUnit.SECONDS));
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.query(queryBuilder);
        Optional.ofNullable(search.getTerminateAfter())
                .ifPresent(searchSourceBuilder::terminateAfter);
        return searchSourceBuilder;
    }

    private void addTagsQueries(BoolQueryBuilder queryBuilder, IdentSearch search) {
        queryBuilder.must(QueryBuilders.matchQuery("tags", search.getTag()));

        Optional.ofNullable(search.getExcludeTags())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.mustNot(QueryBuilders.termsQuery("tags", values));
                    }
                });
    }
}