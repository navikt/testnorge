package no.nav.registre.testnorge.personsearchservice.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.domain.PdlResponse;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.AdresserUtils.addAdresserQueries;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.AlderUtils.addAlderQueries;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.IdentifikasjonUtils.addIdentifikasjonQueries;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.NasjonalitetUtils.addNasjonalitetQueries;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.RelasjonerUtils.addRelasjonerQueries;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.StatusUtils.addStatusQueries;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentSearchAdapter {

    private final ObjectMapper objectMapper;
    private final RestHighLevelClient client;

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
    public PersonList search(PersonSearch search) {
        SearchResponse searchResponse = getSearchResponse(search);

        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        List<Response> responses = convert(searchResponse.getHits().getHits(), Response.class);

        return new PersonList(
                searchResponse.getHits().getTotalHits().value,
                responses.stream().map(Person::new).toList()
        );
    }

    @SneakyThrows
    public PdlResponse searchWithJsonStringResponse(PersonSearch search) {
        SearchResponse searchResponse = getSearchResponse(search);

        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        var searchHits = searchResponse.getHits().getHits();

        var listResponse = Arrays.stream(searchHits).map(SearchHit::getSourceAsString).toList();
        var jsonResponse = JSONArray.toJSONString(listResponse);

        return new PdlResponse(
                searchResponse.getHits().getTotalHits().value,
                jsonResponse
        );
    }

    @SneakyThrows
    private SearchResponse getSearchResponse(PersonSearch search){
        var queryBuilder = QueryBuilders.boolQuery();

        buildQuery(queryBuilder, search);

        var searchSourceBuilder = getSearchSourceBuilder(queryBuilder, search);
        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");
        searchRequest.source(searchSourceBuilder);

        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    private void buildQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addRandomScoreQuery(queryBuilder, search);
        addTagsQueries(queryBuilder, search);
        addIdentifikasjonQueries(queryBuilder, search);
        addAlderQueries(queryBuilder, search);
        addAdresserQueries(queryBuilder, search);
        addNasjonalitetQueries(queryBuilder, search);
        addStatusQueries(queryBuilder, search);
        addRelasjonerQueries(queryBuilder, search);
    }

    private SearchSourceBuilder getSearchSourceBuilder(BoolQueryBuilder queryBuilder, PersonSearch search) {
        var searchSourceBuilder = new SearchSourceBuilder();
        int page = search.getPage();
        int pageSize = search.getPageSize();
        searchSourceBuilder.from((page - 1) * pageSize);
        searchSourceBuilder.timeout(new TimeValue(3, TimeUnit.SECONDS));
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.query(queryBuilder);
        Optional.ofNullable(search.getTerminateAfter())
                .ifPresent(searchSourceBuilder::terminateAfter);
        return searchSourceBuilder;
    }

    private void addRandomScoreQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRandomSeed())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder().seed(value)));
                    }
                });
    }

    private void addTagsQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        queryBuilder.must(QueryBuilders.matchQuery("tags", search.getTag()));

        Optional.ofNullable(search.getExcludeTags())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.mustNot(QueryBuilders.termsQuery("tags", values));
                    }
                });
    }
}