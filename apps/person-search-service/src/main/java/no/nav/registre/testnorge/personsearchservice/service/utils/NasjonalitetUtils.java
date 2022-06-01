package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.NasjonalitetSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedTermsQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.METADATA_FIELD;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.NO;

@UtilityClass
public class NasjonalitetUtils {

    private static final String STATSBORGERSKAP_PATH = "hentPerson.statsborgerskap";
    private static final String INNFLYTTING_PATH = "hentPerson.innflyttingTilNorge";
    private static final String UTFLYTTING_PATH = "hentPerson.utflyttingFraNorge";

    public static void addNasjonalitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getNasjonalitet())
                .ifPresent(value -> {
                    addStatsborgerskapQuery(queryBuilder, value);
                    addUtflyttingQuery(queryBuilder, value);
                    addInnflyttingQuery(queryBuilder, value);

                    if (nonNull(value.getInnflytting())) {
                        addFraflyttingslandQueries(queryBuilder, value.getInnflytting());
                    }
                    if (nonNull(value.getUtflytting())) {
                        addTilflyttingslandQueries(queryBuilder, value.getUtflytting());
                    }
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
                        queryBuilder.must(nestedExistsQuery(UTFLYTTING_PATH, METADATA_FIELD, true));
                    }
                });
    }

    private static void addInnflyttingQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch search) {
        Optional.ofNullable(search.getInnflyttingTilNorge())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery(INNFLYTTING_PATH, METADATA_FIELD, true));
                    }
                });
    }

    private static void addFraflyttingslandQueries(BoolQueryBuilder queryBuilder, NasjonalitetSearch.InnflyttingSearch search) {
        Optional.ofNullable(search.getFraflyttingsland())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(nestedTermsQuery(INNFLYTTING_PATH, ".fraflyttingsland", values, NO));
                    }
                });
        Optional.ofNullable(search.getHistoriskFraflyttingsland())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(nestedTermsQuery(INNFLYTTING_PATH, ".fraflyttingsland", values, YES));
                    }
                });
    }

    private static void addTilflyttingslandQueries(BoolQueryBuilder queryBuilder, NasjonalitetSearch.UtflyttingSearch search) {
        Optional.ofNullable(search.getTilflyttingsland())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(nestedTermsQuery(UTFLYTTING_PATH, ".tilflyttingsland", values, NO));
                    }
                });
        Optional.ofNullable(search.getHistoriskTilflyttingsland())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(nestedTermsQuery(UTFLYTTING_PATH, ".tilflyttingsland", values, YES));
                    }
                });
    }
}

