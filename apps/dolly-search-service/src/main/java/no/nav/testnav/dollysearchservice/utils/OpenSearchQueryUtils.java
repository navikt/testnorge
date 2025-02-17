package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

@UtilityClass
public class OpenSearchQueryUtils {

    public static final String HENT_IDENTER = "hentIdenter.identer";
    public static final String HISTORISK = "historisk";
    public static final String METADATA_HISTORISK = "metadata.historisk";
    public static final String FOLKEREGISTERIDENTIFIKATOR = "hentPerson.folkeregisteridentifikator";
    public static final String NAVSPERSONIDENTIFIKATOR = "hentPerson.navspersonidentifikator";

    public QueryBuilder rangeQuery(String field, Integer value1, Integer value2) {

        return QueryBuilders.rangeQuery(field).from(value1).to(value2);
    }

    public QueryBuilder matchQuery(String field, Object value) {

        return QueryBuilders.matchQuery(field, value);
    }

    public QueryBuilder existQuery(String field) {

        return QueryBuilders.existsQuery(field);
    }

    public QueryBuilder regexpQuery(String field, String value) {

        return QueryBuilders.regexpQuery(field, value);
    }

    public QueryBuilder nestedRegexpQuery(String path, String field, String value) {

        return QueryBuilders.nestedQuery(path, regexpQuery("%s.%s".formatted(path, field), value), ScoreMode.Avg);
    }

    public QueryBuilder nestedMatchQuery(String path, String field, Object value) {

        return QueryBuilders.nestedQuery(path, matchQuery("%s.%s".formatted(path, field), value), ScoreMode.Avg);
    }

    public QueryBuilder nestedExistQuery(String path, String field) {

        return QueryBuilders.nestedQuery(path, existQuery(path + '.' + field), ScoreMode.Avg);
    }
}
