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
        addSivilstandQueries(queryBuilder, search);
    }

    private static void addRelasjonQueries(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        var relasjoner = search.getForelderBarnRelasjoner();
        if (nonNull(relasjoner) && !relasjoner.isEmpty()) {
            for (var relasjon : relasjoner) {
                queryBuilder.must(nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, relasjon, null));
            }
        }
    }


    private static void addBarnQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getHarBarn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        var query = nestedMatchQuery(FORELDER_BARN_RELASJON_PATH, RELATERT_PERSONS_ROLLE, PersonRolle.BARN.toString(), false);
                        if (YES.equalsIgnoreCase(value)) {
                            queryBuilder.must(query);
                        } else if (NO.equalsIgnoreCase(value)) {
                            queryBuilder.mustNot(query);
                        }
                    }
                });
    }

    private static void addDoedfoedtBarnQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getHarDoedfoedtBarn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        var query = nestedExistsQuery(DOEDFOEDT_BARN_PATH, METADATA_FIELD, null);
                        if (YES.equalsIgnoreCase(value)) {
                            queryBuilder.must(query);
                        } else if (NO.equalsIgnoreCase(value)) {
                            queryBuilder.mustNot(query);
                        }
                    }
                });
    }

    private static void addSivilstandQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getSivilstand())
                .ifPresent(value -> {
                    if (nonNull(value.getType()) && !value.getType().isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(SIVILSTAND_PATH, ".type", value.getType(), false));
                    }
                    if (nonNull(value.getTidligereType()) && !value.getTidligereType().isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(SIVILSTAND_PATH, ".type", value.getTidligereType(), true));
                    }
                    if (nonNull(value.getManglerSivilstand()) && Boolean.TRUE.equals(value.getManglerSivilstand())) {
                        queryBuilder.mustNot(nestedExistsQuery(SIVILSTAND_PATH, METADATA_FIELD, null));
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
