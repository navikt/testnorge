package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.FunctionBoostMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RandomScoreFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedRegexpQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedTermsQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.regexpQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class OpenSearchPdlIdenterQueryUtils {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";
    private static final String IDENTIFIKASJONSNUMMER = "identifikasjonsnummer";

    public static FunctionScoreQuery.Builder buildTestnorgeIdentSearchQuery(IdentSearch search) {

        var identer = new HashSet<>(search.getIdenter());
        if (isNotBlank(search.getIdent()) && search.getIdent().length() == 11) {
            identer.add(search.getIdent());
        }

        var queryBuilder = QueryBuilders.bool();

        addTagsQueries(queryBuilder, search.getTags());
        addIdentQuery(queryBuilder, search);
        addNameQuery(queryBuilder, search);
        addIdenterQuery(queryBuilder, identer);

        return new FunctionScoreQuery.Builder()
                .functions(getRandomScoreQuery(search.getSeed()))
                .boostMode(FunctionBoostMode.Replace)
                .query(Query.builder()
                        .bool(queryBuilder.build())
                        .build());
    }

    private static FunctionScore getRandomScoreQuery(Integer seed) {

        return new FunctionScore.Builder()
                .randomScore(new RandomScoreFunction.Builder()
                        .seed(JsonData.of(seed))  // Keeps the randomized order stable across paginated requests.
                        .field("_seq_no")     // Recommended unique field to avoid document duplicates within shards
                        .build())
                .build();
    }

    private static void addTagsQueries(BoolQuery.Builder queryBuilder, List<String> tags) {

        tags.forEach(tag ->
                queryBuilder.must(q -> q.match(matchQuery("tags", tag))));
    }

    private static void addNameQuery(BoolQuery.Builder queryBuilder, IdentSearch search) {

        Optional.ofNullable(search.getNavn())
                .ifPresent(values -> {
                    if (values.size() == 1) {
                        queryBuilder.must(q1 -> q1.nested(QueryBuilders.nested()
                                .path("hentPerson.navn")
                                .query(q2 -> q2.bool(QueryBuilders.bool()
                                        .should(q3 -> q3.regexp(regexpQuery(PERSON_FORNAVN, ".*" + values.getFirst() + ".*")))
                                        .should(q3 -> q3.regexp(regexpQuery(PERSON_ETTERNAVN, ".*" + values.getFirst() + ".*")))
                                        .minimumShouldMatch("1")
                                        .build()))
                                .build()));

                    } else if (values.size() > 1) {
                        queryBuilder.must(q1 -> q1.nested(QueryBuilders.nested()
                                .path("hentPerson.navn")
                                .query(q2 -> q2.bool(QueryBuilders.bool()
                                        .should(q3 -> q3.bool(QueryBuilders.bool()
                                                .must(q4 -> q4.regexp(regexpQuery(PERSON_FORNAVN, ".*" + values.get(0) + ".*")))
                                                .must(q4 -> q4.regexp(regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(1) + ".*")))
                                                .build()))
                                        .should(q3 -> q3.bool(QueryBuilders.bool()
                                                .must(q4 -> q4.regexp(regexpQuery(PERSON_FORNAVN, ".*" + values.get(1) + ".*")))
                                                .must(q4 -> q4.regexp(regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(0) + ".*")))
                                                .build()))
                                        .minimumShouldMatch("1")
                                        .build()))
                                .build()));
                    }
                });
    }

    public static void addIdenterQuery(BoolQuery.Builder queryBuilder, Set<String> identer) {

        if (nonNull(identer) && !identer.isEmpty()) {
            queryBuilder
                    .must(q -> q.nested(nestedTermsQuery(HENT_IDENTER, "ident", identer)));
        }
    }

    private static void addIdentQuery(BoolQuery.Builder queryBuilder, IdentSearch search) {

        Optional.ofNullable(search.getIdent())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(q ->
                                q.nested(nestedRegexpQuery(FOLKEREGISTERIDENTIFIKATOR, IDENTIFIKASJONSNUMMER, ".*" + value + ".*")));
                    }
                });
    }
}
