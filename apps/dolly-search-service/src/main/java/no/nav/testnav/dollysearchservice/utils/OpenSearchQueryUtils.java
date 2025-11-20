package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.NestedQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.RegexpQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQuery;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@UtilityClass
public class OpenSearchQueryUtils {

    public static final String HENT_IDENTER = "hentIdenter.identer";
    public static final String HISTORISK = "historisk";
    public static final String METADATA_HISTORISK = "metadata.historisk";
    public static final String FOLKEREGISTERIDENTIFIKATOR = "hentPerson.folkeregisteridentifikator";
    public static final String NAVSPERSONIDENTIFIKATOR = "hentPerson.navspersonidentifikator";
    public static final String CONCAT = "%s.%s";

    public static RangeQuery rangeQuery(String field, Object value1, Object value2) {

        return QueryBuilders.range()
                .field(field)
                .from(JsonData.of(value1))
                .to(JsonData.of(value2))
                .build();
    }

    public static MatchQuery matchQuery(String field, Object value) {

        return QueryBuilders.match()
                .field(field)
                .query(FieldValue.of(value.toString()))
                .build();
    }

    public static ExistsQuery existQuery(String field) {

        return QueryBuilders.exists().field(field).build();
    }

    public static TermsQuery termsQuery(String field, Set<String> values) {

        val fieldValues = Optional.ofNullable(values).orElse(Collections.emptySet()).stream()
                .map(FieldValue::of)
                .toList();

        return QueryBuilders.terms()
                .field(field)
                .terms(q -> q.value(fieldValues))
                .build();
    }

    public static RegexpQuery regexpQuery(String field, String value) {

        return QueryBuilders.regexp()
                .field(field)
                .value(value)
                .build();
    }

    public static NestedQuery nestedRangeQuery(String path, String field, Object value1, Object value2) {

        return QueryBuilders.nested()
                .path(path)
                .query(q -> q.range(rangeQuery(CONCAT.formatted(path, field), value1, value2)))
                .build();
    }

    public static NestedQuery nestedRegexpQuery(String path, String field, String value) {

        return QueryBuilders.nested()
                .path(path)
                .query(q -> q.regexp(regexpQuery(CONCAT.formatted(path, field), value)))
                .build();
    }

    public static NestedQuery nestedMatchQuery(String path, String field, Object value) {

        return QueryBuilders.nested()
                .path(path)
                .query(q -> q.match(matchQuery(CONCAT.formatted(path, field), value)))
                .build();
    }

    public static NestedQuery nestedTermsQuery(String path, String field, Set<String> values) {

        return QueryBuilders.nested()
                .path(path)
                .query(q -> q.terms(termsQuery(CONCAT.formatted(path, field), values)))
                .build();
    }

    public static NestedQuery nestedExistQuery(String path, String field) {

        return QueryBuilders.nested()
                .path(path)
                .query(q -> q.exists(existQuery(CONCAT.formatted(path, field))))
                .build();
    }

    public static BoolQuery.Builder mustExistQuery(BoolQuery.Builder queryBuilder, String field) {

        return queryBuilder
                .must(q -> q.exists(existQuery(field)));
    }

    public static BoolQuery.Builder mustMatchQuery(BoolQuery.Builder queryBuilder, String field, Object value) {

        return queryBuilder
                .must(q -> q.match(matchQuery(field, value)));
    }
}