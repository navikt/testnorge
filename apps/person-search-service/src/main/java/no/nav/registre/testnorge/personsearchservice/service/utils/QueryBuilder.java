package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.domain.IdentSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static no.nav.registre.testnorge.personsearchservice.service.utils.AdresserUtils.addAdresserQueries;
import static no.nav.registre.testnorge.personsearchservice.service.utils.AlderUtils.addAlderQueries;
import static no.nav.registre.testnorge.personsearchservice.service.utils.IdentifikasjonUtils.addIdentifikasjonQueries;
import static no.nav.registre.testnorge.personsearchservice.service.utils.NasjonalitetUtils.addNasjonalitetQueries;
import static no.nav.registre.testnorge.personsearchservice.service.utils.RelasjonerUtils.addRelasjonerQueries;
import static no.nav.registre.testnorge.personsearchservice.service.utils.StatusUtils.addStatusQueries;

@UtilityClass
public class QueryBuilder {

    private static final String PERSON_FORNAVN = "hentPerson.navn.fornavn";
    private static final String PERSON_ETTERNAVN = "hentPerson.navn.etternavn";

    public static BoolQueryBuilder buildPersonSearchQuery(PersonSearch search) {
        var queryBuilder = QueryBuilders.boolQuery();

        addRandomScoreQuery(queryBuilder, search);
        addTagsQueries(queryBuilder, search.getTag(), search.getTags(), search.getExcludeTags());
        addIdentifikasjonQueries(queryBuilder, search);
        addAlderQueries(queryBuilder, search);
        addAdresserQueries(queryBuilder, search);
        addNasjonalitetQueries(queryBuilder, search);
        addStatusQueries(queryBuilder, search);
        addRelasjonerQueries(queryBuilder, search);

        return queryBuilder;
    }

    public static BoolQueryBuilder buildIdentSearchQuery(IdentSearch search) {
        var queryBuilder = QueryBuilders.boolQuery();

        addTagsQueries(queryBuilder, search.getTag(), search.getTags(), search.getExcludeTags());
        addIdentQuery(queryBuilder, search);
        addNameQuery(queryBuilder, search);

        return queryBuilder;
    }

    private static void addRandomScoreQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRandomSeed())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder().seed(value)));
                    }
                });
    }

    private static void addTagsQueries(BoolQueryBuilder queryBuilder, String tag, List<String> tags, List<String> excludeTags) {
        if (isNull(tag) && (isNull(tags) || tags.isEmpty())) {
            throw new RuntimeException("Mangler tag");
        }

        Optional.ofNullable(tag)
                .ifPresent(value -> queryBuilder.must(QueryBuilders.matchQuery("tags", tag)));

        Optional.ofNullable(tags)
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        for (var val: values){
                            queryBuilder.must(QueryBuilders.matchQuery("tags", val));
                        }
                    }
                });

        Optional.ofNullable(excludeTags)
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.mustNot(QueryBuilders.termsQuery("tags", values));
                    }
                });
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
