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

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedTermsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonSearchAdapter {
    private final ObjectMapper objectMapper;
    private final RestHighLevelClient client;

    private static final String FORELDER_BARN_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String RELATERT_PERSONS_ROLLE = "relatertPersonsRolle";


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
        addKommunenrQuery(queryBuilder, search);
        addPostnrQuery(queryBuilder, search);
        addGeografiskTilknytningQuery(queryBuilder, search);

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

        LocalDate tom = nonNull(fra) ? now.minusYears(fra).minusMonths(3) : now.minusMonths(3);
        LocalDate fom = nonNull(til) ? now.minusYears(til).minusYears(1) : null;

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
                        queryBuilder.must(nestedMatchQuery("hentPerson.kjoenn", "kjoenn", value));
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
                        queryBuilder.must(nestedTermsQuery("hentIdenter.identer", "ident", values));
                    }
                });
    }

    private void addSivilstandQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .flatMap(value -> Optional.ofNullable(value.getType()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.sivilstand", "type", value));
                    }
                });
    }

    private void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getStatsborgerskap())
                .flatMap(value -> Optional.ofNullable(value.getLand()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.statsborgerskap", "land", value));
                    }
                });
    }

    private void addUtflyttingQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getUtflyttingFraNorge())
                .ifPresent(value -> {
                    if (nonNull(value.getUtflyttet()) && Boolean.TRUE.equals(value.getUtflyttet())) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.utflyttingFraNorge", "metadata"));
                    }
                });
    }

    private void addInnflyttingQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getInnflyttingTilNorge())
                .ifPresent(value -> {
                    if (nonNull(value.getInnflytting()) && Boolean.TRUE.equals(value.getInnflytting())) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.innflyttingTilNorge", "metadata"));
                    }
                });
    }

    private void addIdentitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .ifPresent(value -> {
                    if (nonNull(value.getFalskIdentitet()) && value.getFalskIdentitet()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.falskIdentitet", "metadata"));
                    }
                    if (nonNull(value.getUtenlandskIdentitet()) && value.getUtenlandskIdentitet()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.utenlandskIdentifikasjonsnummer", "metadata"));
                    }

                });
    }

    private void addRelasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (nonNull(value.getBarn()) && value.getBarn()) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, "BARN"));
                    }
                    if (nonNull(value.getDoedfoedtBarn()) && value.getDoedfoedtBarn()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.doedfoedtBarn", "metadata"));
                    }
                    if (nonNull(value.getFar()) && value.getFar()) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, "FAR"));
                    }
                    if (nonNull(value.getMor()) && value.getMor()) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, "MOR"));
                    }
                });
    }

    private void addPersonstatusQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getPersonstatus())
                .flatMap(value -> Optional.ofNullable(value.getStatus()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.folkeregisterpersonstatus", "status", value));
                    }
                });
    }

    private void addLevendeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKunLevende())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.mustNot(nestedExistsQuery("hentPerson.doedsfall", "doedsdato"));
                    }
                });
    }

    private void addDoedsfallQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKunDoede())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.doedsfall", "doedsdato"));
                    }
                });
    }

    private void addIdenttypeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .flatMap(value -> Optional.ofNullable(value.getIdenttype()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.folkeregisteridentifikator", "type", value));
                    }
                });
    }

    private void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .flatMap(value -> Optional.ofNullable(value.getAdressebeskyttelse()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.adressebeskyttelse", "gradering", value));
                    }
                });
    }

    private void addKommunenrQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .flatMap(value -> Optional.ofNullable(value.getBostedsadresse()))
                .flatMap(value -> Optional.ofNullable(value.getKommunenummer()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.bostedsadresse",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.matchQuery("hentPerson.bostedsadresse.vegadresse.kommunenummer", value))
                                        .should(QueryBuilders.matchQuery("hentPerson.bostedsadresse.matrikkeladresse.kommunenummer", value))
                                        .must(QueryBuilders.termQuery("hentPerson.bostedsadresse.metadata.historisk", false))
                                        .minimumShouldMatch(1)
                                ,
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private void addPostnrQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .flatMap(value -> Optional.ofNullable(value.getBostedsadresse()))
                .flatMap(value -> Optional.ofNullable(value.getPostnummer()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.bostedsadresse",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.matchQuery("hentPerson.bostedsadresse.vegadresse.postnummer", value))
                                        .should(QueryBuilders.matchQuery("hentPerson.bostedsadresse.matrikkeladresse.postnummer", value))
                                        .must(QueryBuilders.termQuery("hentPerson.bostedsadresse.metadata.historisk", false))
                                        .minimumShouldMatch(1)
                                ,
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private void addGeografiskTilknytningQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getGeografiskTilknytning())
                .flatMap(value -> Optional.ofNullable(value.getGtBydel()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.termQuery("hentGeografiskTilknytning.gtBydel", value));
                    }
                });
    }

    private Optional<RangeQueryBuilder> getBetween(LocalDate fom, LocalDate tom, String field) {
        if (fom == null && tom == null) {
            return Optional.empty();
        }
        var builder = QueryBuilders.rangeQuery(field);

        if (nonNull(fom)) {
            builder.gte(fom);
        }

        if (nonNull(tom)) {
            builder.lte(tom);
        }
        return Optional.of(builder);
    }

}
