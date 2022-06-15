package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.NasjonalitetSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.METADATA_FIELD;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.HISTORISK_PATH;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.NO;

@UtilityClass
public class NasjonalitetUtils {

    private static final String STATSBORGERSKAP_PATH = "hentPerson.statsborgerskap";
    private static final String INNFLYTTING_PATH = "hentPerson.innflyttingTilNorge";
    private static final String UTFLYTTING_PATH = "hentPerson.utflyttingFraNorge";
    private static final List<String> EU_LANDKODER = new ArrayList<>(List.of(
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
    ));

    public static void addNasjonalitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getNasjonalitet())
                .ifPresent(value -> {
                    addStatsborgerskapQuery(queryBuilder, value);
                    addUtflyttingQuery(queryBuilder, value);
                    addInnflyttingQuery(queryBuilder, value);

                    if (nonNull(value.getInnflytting())) {
                        addFraflyttingslandQuery(queryBuilder, value.getInnflytting());
                    }
                    if (nonNull(value.getUtflytting())) {
                        addTilflyttingslandQuery(queryBuilder, value.getUtflytting());
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

    private static void addFraflyttingslandQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch.InnflyttingSearch search) {
        Optional.ofNullable(search.getFraflyttingsland())
                .ifPresent(value -> addLandQuery(queryBuilder, value, INNFLYTTING_PATH, ".fraflyttingsland", NO));
    }


    private static void addTilflyttingslandQuery(BoolQueryBuilder queryBuilder, NasjonalitetSearch.UtflyttingSearch search) {
        Optional.ofNullable(search.getTilflyttingsland())
                .ifPresent(value -> addLandQuery(queryBuilder, value, UTFLYTTING_PATH, ".tilflyttingsland", NO));
    }

    private static void addLandQuery(BoolQueryBuilder queryBuilder, String value, String path, String field, String historisk) {
        if (!value.isEmpty()) {
            var euBoolQuery = getEUBoolQuery(path, field, historisk);
            switch (value) {
                case "V" -> queryBuilder.must(nestedExistsQuery(path, METADATA_FIELD, historisk));
                case "E" -> queryBuilder.must(QueryBuilders.nestedQuery(path, euBoolQuery, ScoreMode.Avg));
                case "U" -> {
                    queryBuilder.must(nestedExistsQuery(path, METADATA_FIELD, historisk));
                    queryBuilder.mustNot(QueryBuilders.nestedQuery(path, euBoolQuery, ScoreMode.Avg));
                }
                default -> queryBuilder.must(nestedMatchQuery(path, field, value, historisk));
            }
        }
    }

    private BoolQueryBuilder getEUBoolQuery(String path, String field, String historisk) {
        var boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(path + HISTORISK_PATH, YES.equalsIgnoreCase(historisk)));
        for (var land : EU_LANDKODER) {
            boolQuery.should(QueryBuilders.matchQuery(path + field, land));
        }
        boolQuery.minimumShouldMatch(1);
        return boolQuery;
    }
}

