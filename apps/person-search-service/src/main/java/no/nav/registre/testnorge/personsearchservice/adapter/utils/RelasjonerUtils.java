package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import no.nav.registre.testnorge.personsearchservice.domain.PersonRolle;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedHistoriskQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.METADATA_FIELD;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.NO;

@UtilityClass
public class RelasjonerUtils {

    private static final String FORELDER_BARN_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String RELATERT_PERSONS_ROLLE = "relatertPersonsRolle";
    private static final String SIVILSTAND_PATH = "hentPerson.sivilstand";
    private static final String DOEDFOEDT_BARN_PATH = "hentPerson.doedfoedtBarn";

    public static void addRelasjonerQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addForeldreQueries(queryBuilder, search);
        addBarnQuery(queryBuilder, search);
        addDoedfoedtBarnQuery(queryBuilder, search);
        addSivilstandQuery(queryBuilder, search);
    }

    private static void addForeldreQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (nonNull(value.getFar()) && Boolean.TRUE.equals(value.getFar())) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.FAR.toString()));
                    }
                    if (nonNull(value.getMor()) && Boolean.TRUE.equals(value.getMor())) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.MOR.toString()));
                    }
                });
    }


    private static void addBarnQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (nonNull(value.getBarn()) && Boolean.TRUE.equals(value.getBarn())) {
                        queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.BARN.toString()));
                    }
                    if (nonNull(value.getHarBarn()) && !value.getHarBarn().isEmpty()) {
                        if (YES.equalsIgnoreCase(value.getHarBarn())) {
                            queryBuilder.must(nestedHistoriskQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.BARN.toString(), false));
                        } else if (NO.equalsIgnoreCase(value.getHarBarn())) {
                            queryBuilder.mustNot(nestedHistoriskQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.BARN.toString(), false));
                        }
                    }
                });
    }

    private static void addDoedfoedtBarnQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    if (nonNull(value.getDoedfoedtBarn()) && Boolean.TRUE.equals(value.getDoedfoedtBarn())) {
                        queryBuilder.must(nestedExistsQuery(DOEDFOEDT_BARN_PATH, METADATA_FIELD));
                    }

                    if (nonNull(value.getHarDoedfoedtBarn()) && !value.getHarDoedfoedtBarn().isEmpty()) {
                        if (YES.equalsIgnoreCase(value.getHarDoedfoedtBarn())) {
                            queryBuilder.must(nestedExistsQuery(DOEDFOEDT_BARN_PATH, METADATA_FIELD));
                        } else if (NO.equalsIgnoreCase(value.getHarDoedfoedtBarn())) {
                            queryBuilder.mustNot(nestedExistsQuery(DOEDFOEDT_BARN_PATH, METADATA_FIELD));
                        }
                    }
                });
    }

    private static void addSivilstandQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .flatMap(value -> Optional.ofNullable(value.getType()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedHistoriskQuery(SIVILSTAND_PATH, "type", value, false));
                    }
                });
    }
}
