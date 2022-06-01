package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.RelasjonSearch;
import no.nav.registre.testnorge.personsearchservice.domain.PersonRolle;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.METADATA_FIELD;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.HISTORISK_PATH;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.NO;

@UtilityClass
public class RelasjonerUtils {

    private static final String FORELDER_BARN_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String RELATERT_PERSONS_ROLLE = ".relatertPersonsRolle";
    private static final String SIVILSTAND_PATH = "hentPerson.sivilstand";
    private static final String DOEDFOEDT_BARN_PATH = "hentPerson.doedfoedtBarn";
    private static final String FORELDREANSVAR_PATH = "hentPerson.foreldreansvar";

    public static void addRelasjonerQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRelasjoner())
                .ifPresent(value -> {
                    addRelasjonQueries(queryBuilder, value);
                    addBarnQuery(queryBuilder, value);
                    addDoedfoedtBarnQuery(queryBuilder, value);
                    addForeldreansvarQuery(queryBuilder, value);
                });
        addSivilstandQuery(queryBuilder, search);
        addManglerSivilstandQuery(queryBuilder, search);
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

    private static void addManglerSivilstandQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .flatMap(value -> Optional.ofNullable(value.getManglerSivilstand()))
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.mustNot(nestedExistsQuery(SIVILSTAND_PATH, ".type", false));
                    }
                });
    }

    private static void addForeldreansvarQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getForeldreansvar())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        var query = QueryBuilders.nestedQuery(
                                FORELDREANSVAR_PATH,
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.matchQuery(FORELDREANSVAR_PATH + ".ansvar", value))
                                        .must(QueryBuilders.existsQuery(FORELDREANSVAR_PATH + ".ansvarlig"))
                                        .must(QueryBuilders.termQuery(FORELDREANSVAR_PATH + HISTORISK_PATH, false))
                                ,
                                ScoreMode.Avg);
                        queryBuilder.must(query);
                    }
                });
    }
}
