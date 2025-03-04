package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class QueryBuilder {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";

    public static BoolQueryBuilder buildIdentSearchQuery(IdentSearch search) {

        var queryBuilder = QueryBuilders.boolQuery();

        addTagsQueries(queryBuilder, search.getTags());
        addIdentQuery(queryBuilder, search);
        addNameQuery(queryBuilder, search);

        return queryBuilder;
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

    private static void addIdentQuery(BoolQueryBuilder queryBuilder, IdentSearch search) {
        Optional.ofNullable(search.getIdent())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.folkeregisteridentifikator",
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.regexpQuery("hentPerson.folkeregisteridentifikator.identifikasjonsnummer", ".*" + value + ".*"))
                                ,
                                ScoreMode.Avg));
                    }
                });
    }

    public static SearchRequest getSearchRequest(BoolQueryBuilder queryBuilder, Integer page, Integer pageSize, Integer terminateAfter) {
        var searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from((page - 1) * pageSize);
        searchSourceBuilder.timeout(new TimeValue(3, TimeUnit.SECONDS));
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.query(queryBuilder);

        Optional.ofNullable(terminateAfter)
                .ifPresent(searchSourceBuilder::terminateAfter);

        var searchRequest = new SearchRequest();
        searchRequest.indices("pdl-sok");
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }
}
