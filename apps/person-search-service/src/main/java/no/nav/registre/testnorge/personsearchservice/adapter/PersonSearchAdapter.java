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

import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;


@Slf4j
@Component
@RequiredArgsConstructor
public class PersonSearchAdapter {
    private final ObjectMapper objectMapper;
    private final RestHighLevelClient client;

    private static final String FORELDER_BARN_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String RELATERT_PERSONS_ROLLE_PATH = FORELDER_BARN_RELASJON_PATH + ".relatertPersonsRolle";


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
        var queryBuilder = QueryBuilders.boolQuery();

        addRandomScoreQuery(queryBuilder, search);
        addTagsQueries(queryBuilder, search);
        addKjoennQuery(queryBuilder, search);
        addLevendeQuery(queryBuilder, search);
        addDoedsfallQuery(queryBuilder, search);
        addFoedselQuery(queryBuilder, search);
        addAlderQuery(queryBuilder, search);
        addIdentQuery(queryBuilder, search);
        addSivilstandQuery(queryBuilder, search);
        addStatsborgerskapQuery(queryBuilder, search);
        addUtflyttingQuery(queryBuilder, search);
        addInnflyttingQuery(queryBuilder, search);
        addIdentitetQueries(queryBuilder, search);
        addRelasjonQueries(queryBuilder, search);
        addPersonstatusQuery(queryBuilder, search);
        addIdenttypeQuery(queryBuilder, search);
        addAdressebeskyttelseQuery(queryBuilder, search);

        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");

        var searchSourceBuilder = new SearchSourceBuilder();
        int page = search.getPage();
        int pageSize = search.getPageSize();
        searchSourceBuilder.from((page - 1) * pageSize);
        searchSourceBuilder.timeout(new TimeValue(3, TimeUnit.SECONDS));
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.query(queryBuilder);
        Optional.ofNullable(search.getTerminateAfter())
                .ifPresent(searchSourceBuilder::terminateAfter);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        List<Response> responses = convert(searchResponse.getHits().getHits(), Response.class);


        return new PersonList(
                searchResponse.getHits().getTotalHits().value,
                responses.stream().map(Person::new).toList()
        );
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

    private void queryAlder(Short fra, Short til, BoolQueryBuilder queryBuilder) {
        LocalDate now = LocalDate.now();

        LocalDate tom = fra != null ? now.minusYears(fra).minusMonths(3) : now.minusMonths(3);
        LocalDate fom = til != null ? now.minusYears(til).minusYears(1) : null;

        queryFoedselsdato(fom, tom, queryBuilder);
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

    private void addKjoennQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKjoenn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.kjoenn",
                                QueryBuilders.matchQuery("hentPerson.kjoenn.kjoenn", value),
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private void addFoedselQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getFoedsel())
                .ifPresent(value -> queryFoedselsdato(value.getFom(), value.getTom(), queryBuilder));
    }

    private void addAlderQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAlder())
                .ifPresent(value -> queryAlder(value.getFra(), value.getTil(), queryBuilder));
    }

    private void addIdentQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdenter())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentIdenter.identer",
                                QueryBuilders.termsQuery("hentIdenter.identer.ident", values),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addSivilstandQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .flatMap(value -> Optional.ofNullable(value.getType()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.sivilstand",
                                QueryBuilders.matchQuery("hentPerson.sivilstand.type", value),
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getStatsborgerskap())
                .flatMap(value -> Optional.ofNullable(value.getLand()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.statsborgerskap",
                                QueryBuilders.matchQuery("hentPerson.statsborgerskap.land", value),
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private void addUtflyttingQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getUtflyttingFraNorge())
                .ifPresent(value -> {
                    if (value.getUtflyttet() != null && value.getUtflyttet()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.utflyttingFraNorge",
                                QueryBuilders.existsQuery("hentPerson.utflyttingFraNorge.metadata"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addInnflyttingQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getInnflyttingTilNorge())
                .ifPresent(value -> {
                    if (value.getInnflytting() != null && value.getInnflytting()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.innflyttingTilNorge",
                                QueryBuilders.existsQuery("hentPerson.innflyttingTilNorge.metadata"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addIdentitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentitet())
                .ifPresent(value -> {
                    if (value.getFalskIdentitet() != null && value.getFalskIdentitet()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.falskIdentitet",
                                QueryBuilders.existsQuery("hentPerson.falskIdentitet.metadata"),
                                ScoreMode.Avg
                        )).must();
                    }
                    if (value.getUtenlandskIdentitet() != null && value.getUtenlandskIdentitet()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.utenlandskIdentifikasjonsnummer",
                                QueryBuilders.existsQuery("hentPerson.utenlandskIdentifikasjonsnummer.metadata"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addRelasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (value.getBarn() != null && value.getBarn()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                FORELDER_BARN_RELASJON_PATH,
                                QueryBuilders.matchQuery(RELATERT_PERSONS_ROLLE_PATH, "BARN"),
                                ScoreMode.Avg
                        )).must();
                    }
                    if (value.getDoedfoedtBarn() != null && value.getDoedfoedtBarn()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.doedfoedtBarn",
                                QueryBuilders.existsQuery("hentPerson.doedfoedtBarn.metadata"),
                                ScoreMode.Avg
                        )).must();
                    }
                    if (value.getFar() != null && value.getFar()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                FORELDER_BARN_RELASJON_PATH,
                                QueryBuilders.matchQuery(RELATERT_PERSONS_ROLLE_PATH, "FAR"),
                                ScoreMode.Avg
                        )).must();
                    }
                    if (value.getMor() != null && value.getMor()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                FORELDER_BARN_RELASJON_PATH,
                                QueryBuilders.matchQuery(RELATERT_PERSONS_ROLLE_PATH, "MOR"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addPersonstatusQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getPersonstatus())
                .flatMap(value -> Optional.ofNullable(value.getStatus()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.folkeregisterpersonstatus",
                                QueryBuilders.matchQuery("hentPerson.folkeregisterpersonstatus.status", value),
                                ScoreMode.Avg
                        ));
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

    private void addDoedsfallQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKunDoede())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.doedsfall",
                                QueryBuilders.existsQuery("hentPerson.doedsfall.doedsdato"),
                                ScoreMode.Avg
                        )).must();
                    }
                });
    }

    private void addIdenttypeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdenttype())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.folkeregisteridentifikator",
                                QueryBuilders.matchQuery("hentPerson.folkeregisteridentifikator.type", value),
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresse())
                .flatMap(value -> Optional.ofNullable(value.getAdressebeskyttelse()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.adressebeskyttelse",
                                QueryBuilders.matchQuery("hentPerson.dressebeskyttelse.gradering", value),
                                ScoreMode.Avg
                        ));
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
