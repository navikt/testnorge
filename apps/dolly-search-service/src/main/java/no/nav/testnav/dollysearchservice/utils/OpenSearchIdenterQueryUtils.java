package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.METADATA_HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.NAVSPERSONIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedMatchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedRegexpQuery;

@UtilityClass
public class OpenSearchIdenterQueryUtils {

    private static final List<Character> NPID_ID = List.of('2', '3', '6', '7');

    public static BoolQueryBuilder addIdenterIdentifier(SearchRequest request) {

        return request.getIdenter().isEmpty() ?

                QueryBuilders.boolQuery()
                        .must(addDollyIdentifier()) :

                QueryBuilders.boolQuery()
                        .must(addIdenterQuery(request));
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

    private static BoolQueryBuilder addIdenterQuery(SearchRequest request) {

        var folkeregisterPids = getFolkeregisterIdenter(request);
        var nPids = getNpids(request);

        if (!folkeregisterPids.isEmpty() && !nPids.isEmpty()) {

            return QueryBuilders.boolQuery()
                    .must(QueryBuilders.boolQuery()
                            .should(QueryBuilders.boolQuery()
                                    .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                                    .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, "type",
                                            request.getPersonRequest().getIdenttype().name()))
                                    .must(QueryBuilders.termsQuery(FOLKEREGISTERIDENTIFIKATOR + ".identifikasjonsnummer", folkeregisterPids))
                            )
                            .should(QueryBuilders.boolQuery()
                                    .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, false))
                                    .must(nestedMatchQuery(HENT_IDENTER, "gruppe", "NPID"))
                                    .must(QueryBuilders.termsQuery(HENT_IDENTER + ".identer", nPids))
                            )
                    );
        } else if (!folkeregisterPids.isEmpty()) {

            QueryBuilders.boolQuery()
                    .must(QueryBuilders.boolQuery()
                            .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                            .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, "type",
                                    request.getPersonRequest().getIdenttype().name()))
                            .must(QueryBuilders.termsQuery(FOLKEREGISTERIDENTIFIKATOR + ".identifikasjonsnummer", folkeregisterPids))
                    );
        } else {

            QueryBuilders.boolQuery()
                    .must(QueryBuilders.boolQuery()
                            .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, false))
                            .must(nestedMatchQuery(HENT_IDENTER, "gruppe", "NPID"))
                            .must(QueryBuilders.termsQuery(HENT_IDENTER + ".identer", nPids))
                    );
        }
        return null;
    }

    private static Set<String> getNpids(SearchRequest request) {

        return request.getIdenter().stream()
                .filter(OpenSearchIdenterQueryUtils::isNpid)
                .collect(Collectors.toSet());
    }

    private static Set<String> getFolkeregisterIdenter(SearchRequest request) {

        return request.getIdenter().stream()
                .filter(ident -> !isNpid(ident))
                .collect(Collectors.toSet());
    }

    private boolean isNpid(String ident) {

        return NPID_ID.contains(ident.charAt(2));
    }
}
