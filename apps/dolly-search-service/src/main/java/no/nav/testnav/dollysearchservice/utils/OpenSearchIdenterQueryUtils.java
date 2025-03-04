package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.METADATA_HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.NAVSPERSONIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedMatchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedRegexpQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedTermsQuery;

@Slf4j
@UtilityClass
public class OpenSearchIdenterQueryUtils {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";
    private static final String IDENTIFIKASJONSNUMMER = "identifikasjonsnummer";

    private static final Random RANDOM = new SecureRandom();

    public static BoolQueryBuilder addIdenterIdentifier(SearchRequest request) {

        var queryBuilder = QueryBuilders.boolQuery();

        if (request.getIdenter().isEmpty()) {

            addDollyIdentifier(queryBuilder);
        } else {

            addIdenterQuery(queryBuilder, request.getIdenter());
        }

        return queryBuilder;
    }

    public static BoolQueryBuilder buildTestnorgeIdentSearchQuery(IdentSearch search) {

        var queryBuilder = QueryBuilders.boolQuery();

        addTagsQueries(queryBuilder, search.getTags());
        addIdentQuery(queryBuilder, search);
        addNameQuery(queryBuilder, search);
        addIdenterQuery(queryBuilder, search.getIdenter());
        addRandomScoreQuery(queryBuilder);

        return queryBuilder;
    }

    private static void addRandomScoreQuery(BoolQueryBuilder queryBuilder) {

        queryBuilder.must(QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder()
                .seed(RANDOM.nextInt())));
    }

    private static void addTagsQueries(BoolQueryBuilder queryBuilder, List<String> tags) {

        tags.forEach(tag -> queryBuilder.must(QueryBuilders.matchQuery("tags", tag)));
    }

    private static void addDollyIdentifier(BoolQueryBuilder queryBuilder) {

        queryBuilder
                .should(matchQuery("tags", "DOLLY"))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedRegexpQuery(FOLKEREGISTERIDENTIFIKATOR, IDENTIFIKASJONSNUMMER, "\\d{2}[4-5]\\d{8}")))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(NAVSPERSONIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedRegexpQuery(NAVSPERSONIDENTIFIKATOR, IDENTIFIKASJONSNUMMER, "\\d{2}[6-7]\\d{8}")));
    }

    private static void addNameQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {
        Optional.ofNullable(search.getNavn())
                .ifPresent(values -> {
                    if (values.size() == 1) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.navn",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.regexpQuery(PERSON_FORNAVN, ".*" + values.get(0) + ".*"))
                                        .should(QueryBuilders.regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(0) + ".*"))
                                        .minimumShouldMatch(1),
                                ScoreMode.Avg));

                    } else if (values.size() > 1) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.navn",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.boolQuery()
                                                .must(QueryBuilders.regexpQuery(PERSON_FORNAVN, ".*" + values.get(0) + ".*"))
                                                .must(QueryBuilders.regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(1) + ".*")))
                                        .should(QueryBuilders.boolQuery()
                                                .must(QueryBuilders.regexpQuery(PERSON_FORNAVN, ".*" + values.get(1) + ".*"))
                                                .must(QueryBuilders.regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(0) + ".*")))
                                        .minimumShouldMatch(1),
                                ScoreMode.Avg));
                    }
                });
    }

    private static void addIdenterQuery(BoolQueryBuilder queryBuilder, Set<String> identer) {

        var now = System.currentTimeMillis();
        var arr = new String[identer.size()];

        queryBuilder
                .should(nestedTermsQuery(HENT_IDENTER, "ident", identer.toArray(arr)));

        log.info("Konvertering av liste til array tok {} ms", System.currentTimeMillis() - now);
    }

    private static void addIdentQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {

        Optional.ofNullable(search.getIdent())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedRegexpQuery(FOLKEREGISTERIDENTIFIKATOR, IDENTIFIKASJONSNUMMER, ".*" + value + ".*"));
                    }
                });
    }
}
