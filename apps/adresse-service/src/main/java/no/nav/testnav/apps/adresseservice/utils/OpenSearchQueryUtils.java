package no.nav.testnav.apps.adresseservice.utils;

import lombok.experimental.UtilityClass;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@UtilityClass
public class OpenSearchQueryUtils {

    public static MatchQuery matchQuery(String field, String value) {

        return QueryBuilders.match()
                .field(field)
                .query(FieldValue.of(value))
                .fuzziness(isNumeric(value) ? "0" : "AUTO")
                .build();
    }

    public static MatchQuery matchQuery(String field, Long value) {

        return QueryBuilders.match()
                .field(field)
                .query(FieldValue.of(value))
                .build();
    }

    public static ExistsQuery existQuery(String field) {

        return QueryBuilders.exists().field(field).build();
    }
}