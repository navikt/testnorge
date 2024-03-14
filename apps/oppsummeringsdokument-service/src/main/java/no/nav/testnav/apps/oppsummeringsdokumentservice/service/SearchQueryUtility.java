package no.nav.testnav.apps.oppsummeringsdokumentservice.service;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.QueryRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class SearchQueryUtility {

    public static QueryBuilder prepareQuery(QueryRequest request) {

        if (isNull(request)) {
            return QueryBuilders.matchAllQuery();
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        matchString(query, "_id", request.getId());
        matchString(query, "miljo", request.getMiljo());
        matchString(query, "opplysningspliktigOrganisajonsnummer", request.getOrgnummer());
        matchString(query, "virksomheter.personer.ident", request.getIdent());
        matchString(query, "virksomheter.personer.arbeidsforhold.typeArbeidsforhold", request.getTypeArbeidsforhold());

        if (nonNull(request.getFom())) {
            query.must(QueryBuilders.rangeQuery("kalendermaaned").gte(request.getFom().withDayOfMonth(1)));
        }

        if (nonNull(request.getTom())) {
            query.must(QueryBuilders.rangeQuery("kalendermaaned").lte(request.getTom().withDayOfMonth(request.getTom().lengthOfMonth())));
        }

        return query;
    }

    private static void matchString(BoolQueryBuilder builder, String field, String value) {

        if (isNotBlank(value)) {
            builder.must(QueryBuilders.matchQuery(field, value));
        }
    }
}
