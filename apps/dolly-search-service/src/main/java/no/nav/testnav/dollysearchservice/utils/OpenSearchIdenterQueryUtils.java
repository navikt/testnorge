package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.Set;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.METADATA_HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.NAVSPERSONIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedMatchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedRegexpQuery;

@UtilityClass
public class OpenSearchIdenterQueryUtils {

    public static BoolQueryBuilder addIdenterIdentifier(SearchRequest request) {

        return request.getIdenter().isEmpty() ?

                QueryBuilders.boolQuery()
                        .must(addDollyIdentifier()) :

                QueryBuilders.boolQuery()
                        .must(addIdenterQuery(request.getIdenter()));
    }

    private static BoolQueryBuilder addDollyIdentifier() {

        return QueryBuilders.boolQuery()
                .should(matchQuery("tags", "DOLLY"))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedRegexpQuery(FOLKEREGISTERIDENTIFIKATOR, "identifikasjonsnummer", "\\d{2}[4-5]\\d{8}")))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(NAVSPERSONIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedRegexpQuery(NAVSPERSONIDENTIFIKATOR, "identifikasjonsnummer", "\\d{2}[6-7]\\d{8}")));
    }

    private static BoolQueryBuilder addIdenterQuery(Set<String> identer) {

        var query = QueryBuilders.boolQuery();
        identer.forEach(ident -> query
                .should(nestedMatchQuery(HENT_IDENTER, "ident", ident))
        );
        return query;
    }
}
