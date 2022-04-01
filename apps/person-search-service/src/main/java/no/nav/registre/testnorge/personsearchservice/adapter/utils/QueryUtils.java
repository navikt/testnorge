package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;

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
}
