package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

@UtilityClass
public class QueryUtils {

    public static NestedQueryBuilder nestedMatchQuery(String path, String name, String value) {
        return QueryBuilders.nestedQuery(
                path,
                QueryBuilders.matchQuery(path + "." + name, value),
                ScoreMode.Avg
        );
    }
}
