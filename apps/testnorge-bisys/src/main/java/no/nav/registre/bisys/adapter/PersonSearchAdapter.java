package no.nav.registre.bisys.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.adapter.model.Response;
import no.nav.registre.bisys.domain.search.PersonSearch;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@RequiredArgsConstructor
public class PersonSearchAdapter {
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
    public List<Response> search(PersonSearch search) {
        var queryBuilder = QueryBuilders.boolQuery();

        addRandomScoreQuery(queryBuilder, search);
        addTagsQueries(queryBuilder, search);
        addLevendeQuery(queryBuilder, search);
        addFoedselQuery(queryBuilder, search);
        addRelasjonQueries(queryBuilder, search);

        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");

        var searchSourceBuilder = new SearchSourceBuilder();
        int page = search.getPage();
        int pageSize = search.getPageSize();
        searchSourceBuilder.from((page - 1) * pageSize);
        searchSourceBuilder.timeout(new TimeValue(3, TimeUnit.SECONDS));
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.query(queryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        return convert(searchResponse.getHits().getHits(), Response.class);
    }

    private void queryFoedselsdato(LocalDate fom, LocalDate tom, BoolQueryBuilder queryBuilder) {
        getBetween(fom, tom, "hentPerson.foedsel.foedselsdato")
                .ifPresent(rangeQueryBuilder -> queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.foedsel",
                                rangeQueryBuilder,
                                ScoreMode.Avg
                        ))
                );
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


    private void addFoedselQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getFoedsel())
                .ifPresent(value -> queryFoedselsdato(value.getFom(), value.getTom(), queryBuilder));
    }

    private void addRelasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (value.getFar() != null && value.getFar()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.forelderBarnRelasjon",
                                QueryBuilders.matchQuery("hentPerson.forelderBarnRelasjon.relatertPersonsRolle", "FAR"),
                                ScoreMode.Avg
                        )).must();
                    }
                    if (value.getMor() != null && value.getMor()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.forelderBarnRelasjon",
                                QueryBuilders.matchQuery("hentPerson.forelderBarnRelasjon.relatertPersonsRolle", "MOR"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addLevendeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKunLevende())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.mustNot(QueryBuilders.nestedQuery(
                                "hentPerson.doedsfall",
                                QueryBuilders.existsQuery("hentPerson.doedsfall.doedsdato"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private Optional<RangeQueryBuilder> getBetween(LocalDate fom, LocalDate tom, String field) {
        if (fom == null && tom == null) {
            return Optional.empty();
        }
        var builder = QueryBuilders.rangeQuery(field);

        if (fom != null) {
            builder.gte(fom);
        }

        if (tom != null) {
            builder.lte(tom);
        }
        return Optional.of(builder);
    }

}
