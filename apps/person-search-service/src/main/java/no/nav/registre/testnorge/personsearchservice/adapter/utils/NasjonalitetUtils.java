package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedHistoriskQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.METADATA_FIELD;

@UtilityClass
public class NasjonalitetUtils {

    private static final String STATSBORGERSKAP_PATH = "hentPerson.statsborgerskap";

    public static void addNasjonalitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addStatsborgerskapQuery(queryBuilder, search);
        addUtflyttingQuery(queryBuilder, search);
        addInnflyttingQuery(queryBuilder, search);
    }

    private static void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getStatsborgerskap())
                .flatMap(value -> Optional.ofNullable(value.getLand()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedHistoriskQuery(STATSBORGERSKAP_PATH, "land", value, false));
                    }
                });
    }

    private static void addUtflyttingQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getUtflyttingFraNorge())
                .ifPresent(value -> {
                    if (nonNull(value.getUtflyttet()) && Boolean.TRUE.equals(value.getUtflyttet())) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.utflyttingFraNorge", METADATA_FIELD));
                    }
                });
    }

    private static void addInnflyttingQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getInnflyttingTilNorge())
                .ifPresent(value -> {
                    if (nonNull(value.getInnflytting()) && Boolean.TRUE.equals(value.getInnflytting())) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.innflyttingTilNorge", METADATA_FIELD));
                    }
                });
    }
}

