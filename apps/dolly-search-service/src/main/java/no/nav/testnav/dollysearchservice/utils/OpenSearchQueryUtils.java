package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQueryField;
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

    public static Query.Builder rangeQuery(String field, Object value1, Object value2) {

        return QueryBuilders.range()
                .field(field)
                .from(FieldValue.of(value1))
                        .to(FieldValue.of(value2));
    }

    public static Query.Builder matchQuery(String field, Object value) {

        return QueryBuilders.match()
                .field(field)
                .query(FieldValue.of(value.toString()))
    }

    public static ExistsQuery.Builder existQuery(String field) {

        return QueryBuilders.exists().field(field);
    }

    public static Query.Builder termsQuery(String field, Object[] values) {

        new FieldValue(values)
        return QueryBuilders.terms()
                .field(field)
                .terms(TermsQueryField.of(builder -> builder.value(FieldValue.of(values)));
    }

    public static QueryBuilders regexpQuery(String field, String value) {

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