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
    public static final String CONCAT = "%s.%s";

    public static QueryBuilder rangeQuery(String field, Object value1, Object value2) {

        return QueryBuilders.rangeQuery(field).from(value1).to(value2);
    }

    public static QueryBuilder matchQuery(String field, Object value) {

        return QueryBuilders.matchQuery(field, value);
    }

    public static QueryBuilder existQuery(String field) {

        return QueryBuilders.existsQuery(field);
    }

    public static QueryBuilder termsQuery(String field, Object[] values) {

        return QueryBuilders.termsQuery(field, values);
    }

    public static QueryBuilder regexpQuery(String field, String value) {

        return QueryBuilders.regexpQuery(field, value);
    }

    public static QueryBuilder nestedRegexpQuery(String path, String field, String value) {

        return QueryBuilders.nestedQuery(path, regexpQuery(CONCAT.formatted(path, field), value), ScoreMode.Avg);
    }

    public static QueryBuilder nestedMatchQuery(String path, String field, Object value) {

        return QueryBuilders.nestedQuery(path, matchQuery(CONCAT.formatted(path, field), value), ScoreMode.Avg);
    }

    public static QueryBuilder nestedTermsQuery(String path, String field, Object[] values) {

        return QueryBuilders.nestedQuery(path, termsQuery(CONCAT.formatted(path, field), values), ScoreMode.Avg);
    }

    public static QueryBuilder nestedExistQuery(String path, String field) {

        return QueryBuilders.nestedQuery(path, existQuery(CONCAT.formatted(path, field)), ScoreMode.Avg);
    }
}