package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@UtilityClass
public class QueryUtils {

    public static final String METADATA_FIELD = ".metadata";
    public static final String HISTORISK_PATH = ".metadata.historisk";

    public static final String YES = "Y";
    public static final String NO = "N";


    public static NestedQueryBuilder nestedTermsQuery(String path, String field, Collection<String> values, boolean historisk) {
        if (historisk){
            return QueryBuilders.nestedQuery(path, QueryBuilders.termsQuery(path + field, values), ScoreMode.Avg);
        }else{
            return QueryBuilders.nestedQuery(
                    path,
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.termsQuery(path + field, values))
                            .must(QueryBuilders.termQuery(path + HISTORISK_PATH, false))
                    ,
                    ScoreMode.Avg
            );
        }
    }

    public static NestedQueryBuilder nestedExistsQuery(String path, String field, boolean historisk) {
        if (historisk) {
            return QueryBuilders.nestedQuery(path, QueryBuilders.existsQuery(path + field), ScoreMode.Avg);
        } else {
            return QueryBuilders.nestedQuery(
                    path,
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.existsQuery(path + field))
                            .must(QueryBuilders.termQuery(path + HISTORISK_PATH, false))
                    ,
                    ScoreMode.Avg
            );
        }

    }

    public static NestedQueryBuilder nestedMatchQuery(String path, String field, String value, boolean historisk) {
        if (historisk) {
            return QueryBuilders.nestedQuery(path, QueryBuilders.matchQuery(path + field, value), ScoreMode.Avg);
        } else {
            return QueryBuilders.nestedQuery(
                    path,
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.matchQuery(path + field, value))
                            .must(QueryBuilders.termQuery(path + HISTORISK_PATH, false))
                    ,
                    ScoreMode.Avg);
        }
    }

    public static NestedQueryBuilder nestedShouldMatchQuery(
            String path,
            List<String> fields,
            String value,
            int minimumShould,
            boolean historisk
    ) {
        var boolQuery = QueryBuilders.boolQuery();

        for (String field : fields) {
            boolQuery.should(QueryBuilders.matchQuery(path + field, value));
        }
        if (!historisk) {
            boolQuery.must(QueryBuilders.termQuery(path + HISTORISK_PATH, false));
        }
        boolQuery.minimumShouldMatch(minimumShould);

        return QueryBuilders.nestedQuery(path, boolQuery, ScoreMode.Avg);
    }


    public static NestedQueryBuilder nestedShouldExistQuery(
            String path,
            List<String> fields,
            int minimumShould,
            boolean historisk
    ) {
        var boolQuery = QueryBuilders.boolQuery();

        for (String field : fields) {
            if (historisk) {
                boolQuery.should(QueryBuilders.existsQuery(path + field));
            } else {
                boolQuery.should(
                        (QueryBuilders.boolQuery()
                                .must(QueryBuilders.existsQuery(path + field))
                                .must(QueryBuilders.termQuery(path + HISTORISK_PATH, false))));
            }
        }

        boolQuery.minimumShouldMatch(minimumShould);

        return QueryBuilders.nestedQuery(path, boolQuery, ScoreMode.Avg);
    }

    public static Optional<RangeQueryBuilder> getBetween(LocalDate fom, LocalDate tom, String field) {
        if (fom == null && tom == null) {
            return Optional.empty();
        }
        var builder = QueryBuilders.rangeQuery(field);

        if (nonNull(fom)) {
            builder.gte(fom);
        }

        if (nonNull(tom)) {
            builder.lte(tom);
        }
        return Optional.of(builder);
    }
}
