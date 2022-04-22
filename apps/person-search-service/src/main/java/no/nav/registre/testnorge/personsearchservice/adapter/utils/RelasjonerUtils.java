package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import no.nav.registre.testnorge.personsearchservice.controller.search.RelasjonSearch;
import no.nav.registre.testnorge.personsearchservice.domain.PersonRolle;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.METADATA_FIELD;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.NO;

@UtilityClass
public class RelasjonerUtils {

    private static final String FORELDER_BARN_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String RELATERT_PERSONS_ROLLE = ".relatertPersonsRolle";
    private static final String SIVILSTAND_PATH = "hentPerson.sivilstand";
    private static final String DOEDFOEDT_BARN_PATH = "hentPerson.doedfoedtBarn";

    public static void addRelasjonerQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    addRelasjonQueries(queryBuilder, value);
                    addBarnQuery(queryBuilder, value);
                    addDoedfoedtBarnQuery(queryBuilder, value);
                });
        addSivilstandQuery(queryBuilder, search);
    }

    private static void addRelasjonQueries(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        var relasjoner = search.getForelderBarnRelasjoner();
        if (nonNull(relasjoner) && !relasjoner.isEmpty()) {
            for (var relasjon : relasjoner) {
                queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, relasjon, true));
            }
        }
    }


    private static void addBarnQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getHarBarn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        if (YES.equalsIgnoreCase(value)) {
                            queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.BARN.toString(), false));
                        } else if (NO.equalsIgnoreCase(value)) {
                            queryBuilder.mustNot(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.BARN.toString(), false));
                        }
                    }
                });
    }

    private static void addDoedfoedtBarnQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getHarDoedfoedtBarn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        if (YES.equalsIgnoreCase(value)) {
                            queryBuilder.must(nestedExistsQuery(DOEDFOEDT_BARN_PATH, METADATA_FIELD, true));
                        } else if (NO.equalsIgnoreCase(value)) {
                            queryBuilder.mustNot(nestedExistsQuery(DOEDFOEDT_BARN_PATH, METADATA_FIELD, true));
                        }
                    }
                });
    }

    private static void addSivilstandQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .flatMap(value -> Optional.ofNullable(value.getType()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(SIVILSTAND_PATH, ".type", value, false));
                    }
                });
    }
}
