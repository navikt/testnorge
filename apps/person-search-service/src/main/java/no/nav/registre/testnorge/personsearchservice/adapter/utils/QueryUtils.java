package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.nonNull;

@UtilityClass
public class QueryUtils {

    public static NestedQueryBuilder nestedMatchQuery(String path, String field, String value) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.matchQuery(path + "." + field, value),
                ScoreMode.Avg
        );
    }

    public static NestedQueryBuilder nestedTermsQuery(String path, String field, Collection<String> values) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.termsQuery(path + "." + field, values),
                ScoreMode.Avg
        );
    }

    public static NestedQueryBuilder nestedExistsQuery(String path, String field) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.existsQuery(path + "." + field),
                ScoreMode.Avg
        );
    }

    public static NestedQueryBuilder nestedHistoriskQuery(String path, String field, String value, boolean historisk) {
        if (historisk) {
            return nestedMatchQuery(path, field, value);
        } else {
            return QueryBuilders.nestedQuery(path, boolMatchQuery(path, field, value), ScoreMode.Avg);
        }
    }

    private static BoolQueryBuilder boolMatchQuery(String path, String field, String value) {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(path + "." + field, value))
                .must(QueryBuilders.termQuery(path + ".metadata.historisk", false));

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
