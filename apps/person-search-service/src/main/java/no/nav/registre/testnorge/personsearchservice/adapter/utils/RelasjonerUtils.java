package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.boolMatchQuery;

@UtilityClass
public class RelasjonerUtils {

    private static final String FORELDER_BARN_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String RELATERT_PERSONS_ROLLE = "relatertPersonsRolle";
    private static final String SIVILSTAND_PATH = "hentPerson.sivilstand";

    public static void addRelasjonerQueries(BoolQueryBuilder queryBuilder, PersonSearch search){
        addRelasjonQueries(queryBuilder, search);
        addSivilstandQuery(queryBuilder, search);
    }

    private static void addRelasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (nonNull(value.getBarn()) && Boolean.TRUE.equals(value.getBarn())) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, "BARN"));
                    }
                    if (nonNull(value.getDoedfoedtBarn()) && Boolean.TRUE.equals(value.getDoedfoedtBarn())) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.doedfoedtBarn", "metadata"));
                    }
                    if (nonNull(value.getFar()) && Boolean.TRUE.equals(value.getFar())) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, "FAR"));
                    }
                    if (nonNull(value.getMor()) && Boolean.TRUE.equals(value.getMor())) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, "MOR"));
                    }
                });
    }

    private static void addSivilstandQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .flatMap(value -> Optional.ofNullable(value.getType()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                SIVILSTAND_PATH,
                                boolMatchQuery(SIVILSTAND_PATH, "type", value, false),
                                ScoreMode.Avg
                        ));
                    }
                });
    }
}
