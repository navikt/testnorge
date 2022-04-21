package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.NasjonalitetSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.METADATA_FIELD;

@UtilityClass
public class NasjonalitetUtils {

    private static final String STATSBORGERSKAP_PATH = "hentPerson.statsborgerskap";

    public static void addNasjonalitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getNasjonalitet())
                .ifPresent(value -> {
                    addStatsborgerskapQuery(queryBuilder, value);
                    addUtflyttingQuery(queryBuilder, value);
                    addInnflyttingQuery(queryBuilder, value);
                });
    }

    private static void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch search) {
        Optional.ofNullable(search.getStatsborgerskap())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(STATSBORGERSKAP_PATH, ".land", value, false));
                    }
                });
    }

    private static void addUtflyttingQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch search) {
        Optional.ofNullable(search.getUtflyttingFraNorge())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.utflyttingFraNorge", METADATA_FIELD, true));
                    }
                });
    }

    private static void addInnflyttingQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch search) {
        Optional.ofNullable(search.getInnflyttingTilNorge())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.innflyttingTilNorge", METADATA_FIELD, true));
                    }
                });
    }
}

