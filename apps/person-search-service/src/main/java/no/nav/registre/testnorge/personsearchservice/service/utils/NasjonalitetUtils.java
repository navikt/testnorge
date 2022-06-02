package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.NasjonalitetSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Arrays;
import java.util.Collection;
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
    private static final Collection<String> EØS_LANDKODER = Arrays.asList(
            "AUT",
            "BEL",
            "BGR",
            "CHE",
            "CYP",
            "CZE",
            "DEU",
            "DNK",
            "ESP",
            "EST",
            "FIN",
            "FRA",
            "GBR",
            "GRC",
            "HRV",
            "HUN",
            "IRL",
            "ISL",
            "ITA",
            "LIE",
            "LTU",
            "LUX",
            "LVA",
            "MLT",
            "NLD",
            "POL",
            "PRT",
            "ROU",
            "SVK",
            "SVN",
            "SWE"
    );

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
                        queryBuilder.must(nestedMatchQuery(STATSBORGERSKAP_PATH, ".land", value, NO));
                    }
                });
    }

    private static void addUtflyttingQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch search) {
        Optional.ofNullable(search.getUtflyttingFraNorge())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery(UTFLYTTING_PATH, METADATA_FIELD, ""));
                    }
                });
    }

    private static void addInnflyttingQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch search) {
        Optional.ofNullable(search.getInnflyttingTilNorge())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery(INNFLYTTING_PATH, METADATA_FIELD, ""));
                    }
                });
    }

    private static void addFraflyttingslandQueries(BoolQueryBuilder queryBuilder, NasjonalitetSearch.InnflyttingSearch search) {
        Optional.ofNullable(search.getFraflyttingsland())
                .ifPresent(value -> {
                    addLandQuery(queryBuilder, value, INNFLYTTING_PATH, ".fraflyttingsland", NO);
                });
        Optional.ofNullable(search.getHistoriskFraflyttingsland())
                .ifPresent(value -> {
                    addLandQuery(queryBuilder, value, INNFLYTTING_PATH, ".fraflyttingsland", YES);
                });
    }


    private static void addTilflyttingslandQueries(BoolQueryBuilder queryBuilder, NasjonalitetSearch.UtflyttingSearch search) {
        Optional.ofNullable(search.getTilflyttingsland())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        addLandQuery(queryBuilder, value, UTFLYTTING_PATH, ".tilflyttingsland", NO);
                    }
                });
        Optional.ofNullable(search.getHistoriskTilflyttingsland())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        addLandQuery(queryBuilder, value, UTFLYTTING_PATH, ".tilflyttingsland", YES);
                    }
                });
    }

    private static void addLandQuery(BoolQueryBuilder queryBuilder, String value, String path, String field, String historisk) {
        if (!value.isEmpty()) {
            switch (value) {
                case "VERDEN" -> queryBuilder.must(nestedExistsQuery(path, METADATA_FIELD, historisk));
                case "EØS", "EU" -> queryBuilder.must(nestedTermsQuery(path, field, EØS_LANDKODER, historisk));
                case "UEØS" -> queryBuilder.mustNot(nestedTermsQuery(path, field, EØS_LANDKODER, historisk));
                default -> queryBuilder.must(nestedMatchQuery(path, field, value, historisk));
            }
        }
    }
}

