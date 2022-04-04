package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.nonNull;

@UtilityClass
public class QueryUtils {

    public static NestedQueryBuilder nestedMatchQuery(String path, String name, String value) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.matchQuery(path + "." + name, value),
                ScoreMode.Avg
        );
    }

    public static NestedQueryBuilder nestedTermsQuery(String path, String name, Collection<String> values) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.termsQuery(path + "." + name, values),
                ScoreMode.Avg
        );
    }

    public static NestedQueryBuilder nestedExistsQuery(String path, String name) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.existsQuery(path + "." + name),
                ScoreMode.Avg
        );
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
