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
    private static final String DELT_BOSTED_PATH = "hentPerson.deltBosted";

    public static void addRelasjonerQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    addForeldreQueries(queryBuilder, value);
                    addBarnQuery(queryBuilder, value);
                    addDoedfoedtBarnQuery(queryBuilder, value);
                    addDeltBostedQuery(queryBuilder, value);
                });
        addSivilstandQuery(queryBuilder, search);
    }

    private static void addForeldreQueries(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        var relasjoner = search.getForelderRelasjoner();
        if (nonNull(relasjoner) && !relasjoner.isEmpty()){
            if (relasjoner.contains(PersonRolle.FAR.toString())){
                queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.FAR.toString(), true));
            }
            if (relasjoner.contains(PersonRolle.MOR.toString())){
                queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.MOR.toString(), true));
            }
            if (relasjoner.contains(PersonRolle.MEDMOR.toString())){
                queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.MEDMOR.toString(), true));
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


    private static void addDeltBostedQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getDeltBosted())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        if (YES.equalsIgnoreCase(value)) {
                            queryBuilder.must(nestedExistsQuery(DELT_BOSTED_PATH, METADATA_FIELD, false));
                        } else if (NO.equalsIgnoreCase(value)) {
                            queryBuilder.mustNot(nestedExistsQuery(DELT_BOSTED_PATH, METADATA_FIELD, false));
                        }
                    }
                });
    }
}
