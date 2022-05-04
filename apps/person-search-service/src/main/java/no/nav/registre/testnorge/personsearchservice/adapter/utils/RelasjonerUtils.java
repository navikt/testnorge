package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.RelasjonSearch;
import no.nav.registre.testnorge.personsearchservice.domain.PersonRolle;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.*;

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
                    addMinNumberBarnQuery(queryBuilder, value);
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

    private static void addMinNumberBarnQuery(BoolQueryBuilder queryBuilder, RelasjonSearch search) {
        Optional.ofNullable(search.getMinNumberBarn())
                .ifPresent(value -> {
                    if (value > 0) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("limit", value);
                        var script = new Script(
                                ScriptType.INLINE,
                                "painless",
                                "def relasjoner = doc['hentPerson.forelderBarnRelasjon']; if (relasjoner != null){ return relasjoner.filter(relasjon => relasjon.relatertPersonsRolle == 'BARN').length == params.limit; } return false;",
                                Collections.emptyMap(),
                                params);
                        queryBuilder.must(nestedScriptQuery(FORELDER_BARN_RELASJON_PATH, script));
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
