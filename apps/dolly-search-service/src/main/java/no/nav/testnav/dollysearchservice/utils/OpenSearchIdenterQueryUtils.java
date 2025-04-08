package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedRegexpQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedTermsQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.regexpQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class OpenSearchIdenterQueryUtils {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";
    private static final String IDENTIFIKASJONSNUMMER = "identifikasjonsnummer";

    private static final Random RANDOM = new SecureRandom();

    public static BoolQueryBuilder buildTestnorgeIdentSearchQuery(IdentSearch search) {

        var identer = new HashSet<>(search.getIdenter());
        if (isNotBlank(search.getIdent()) && search.getIdent().length() == 11) {
            identer.add(search.getIdent());
        }

        var queryBuilder = QueryBuilders.boolQuery();

        addTagsQueries(queryBuilder, search.getTags());
        addIdentQuery(queryBuilder, search);
        addNameQuery(queryBuilder, search);
        addIdenterQuery(queryBuilder, identer);
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

    private static void addNameQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {

        Optional.ofNullable(search.getNavn())
                .ifPresent(values -> {
                    if (values.size() == 1) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.navn",
                                QueryBuilders.boolQuery()
                                        .should(regexpQuery(PERSON_FORNAVN, ".*" + values.getFirst() + ".*"))
                                        .should(regexpQuery(PERSON_ETTERNAVN, ".*" + values.getFirst() + ".*"))
                                        .minimumShouldMatch(1),
                                ScoreMode.Avg));

                    } else if (values.size() > 1) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.navn",
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.boolQuery()
                                                .must(regexpQuery(PERSON_FORNAVN, ".*" + values.get(0) + ".*"))
                                                .must(regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(1) + ".*")))
                                        .should(QueryBuilders.boolQuery()
                                                .must(regexpQuery(PERSON_FORNAVN, ".*" + values.get(1) + ".*"))
                                                .must(regexpQuery(PERSON_ETTERNAVN, ".*" + values.get(0) + ".*")))
                                        .minimumShouldMatch(1),
                                ScoreMode.Avg));
                    }
                });
    }

    public static void addIdenterQuery(BoolQueryBuilder queryBuilder, Set<String> identer) {

        queryBuilder
                .must(nestedTermsQuery(HENT_IDENTER, "ident", identer.toArray(String[]::new)));
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
