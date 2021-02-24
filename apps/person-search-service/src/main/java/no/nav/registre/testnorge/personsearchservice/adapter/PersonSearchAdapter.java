package no.nav.registre.testnorge.personsearchservice.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.controller.dto.Pageing;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import no.nav.registre.testnorge.personsearchservice.domain.Search;

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
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    public PersonList search(Search search) {
        var queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.matchQuery("tags", search.getTag()));

        Optional.ofNullable(search.getKjoenn())
                .ifPresent(value -> queryBuilder.must(QueryBuilders.nestedQuery(
                        "hentPerson.kjoenn",
                        QueryBuilders.matchQuery("hentPerson.kjoenn.kjoenn", value),
                        ScoreMode.Avg
                )));

        Optional.ofNullable(search.getFoedsel()).flatMap(value -> getBetween(value.getFom(), value.getTom(), "hentPerson.foedsel.foedselsdato"))
                .ifPresent(rangeQueryBuilder -> queryBuilder.must(QueryBuilders.nestedQuery(
                        "hentPerson.foedsel",
                        rangeQueryBuilder,
                        ScoreMode.Avg
                        )
                ));

        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");

        var searchSourceBuilder = new SearchSourceBuilder();
        Pageing page = search.getPageing();
        searchSourceBuilder.from((page.getPage() - 1) * page.getPageSize());
        searchSourceBuilder.size(page.getPageSize());
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(searchSourceBuilder);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        List<Response> responses = convert(searchResponse.getHits().getHits(), Response.class);


        return new PersonList(
                searchResponse.getHits().getTotalHits().value,
                responses.stream().map(Person::new).collect(Collectors.toList())
        );
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
