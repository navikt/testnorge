package no.nav.testnav.apps.adresseservice.utils;

import lombok.experimental.UtilityClass;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.FuzzyQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;

@UtilityClass
public class OpenSearchQueryUtils {

    public static MatchQuery matchQuery(String field, Object value) {

        return QueryBuilders.match()
                .field(field)
                .query(FieldValue.of(value.toString()))
                .build();
    }

    public static ExistsQuery existQuery(String field) {

        return QueryBuilders.exists().field(field).build();
    }

    public static FuzzyQuery fuzzyQuery(String field, Object object) {

        return QueryBuilders.fuzzy()
                .field(field)
                .value(FieldValue.of(object.toString()))
                .fuzziness("2")
                .build();
    }
}