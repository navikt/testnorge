package no.nav.dolly.elastic.service;

import lombok.experimental.UtilityClass;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class QueryBuilder {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";

    private Random random = new SecureRandom();

    public static BoolQueryBuilder buildPersonSearchQuery() {

        var queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder()
                        .seed(random.nextLong())));
        queryBuilder.must(QueryBuilders.existsQuery("aareg"));

        return queryBuilder;
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
        searchRequest.indices("bestilling");
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

}
