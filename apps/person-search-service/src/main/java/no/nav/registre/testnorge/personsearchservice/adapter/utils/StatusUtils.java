package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;

@UtilityClass
public class StatusUtils {

    public static void addStatusQueries(BoolQueryBuilder queryBuilder, PersonSearch search){
        addPersonstatusQuery(queryBuilder, search);
        addLevendeQuery(queryBuilder, search);
    }

    private static void addPersonstatusQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getPersonstatus())
                .flatMap(value -> Optional.ofNullable(value.getStatus()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.folkeregisterpersonstatus",
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.matchQuery("hentPerson.folkeregisterpersonstatus.status", value))
                                        .must(QueryBuilders.termQuery("hentPerson.folkeregisterpersonstatus.metadata.historisk", false))
                                ,
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private static void addLevendeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKunLevende())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.mustNot(nestedExistsQuery("hentPerson.doedsfall", "doedsdato"));
                    }
                });
    }

}
