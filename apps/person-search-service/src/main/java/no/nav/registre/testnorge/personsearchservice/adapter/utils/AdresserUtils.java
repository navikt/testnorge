package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.apache.lucene.search.join.ScoreMode;

import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.HISTORISK_PATH;

@UtilityClass
public class AdresserUtils {
    private static final String BOSTEDSADRESSE_PATH = "hentPerson.bostedsadresse";
    private static final String B_VEGADRESSE_PATH = BOSTEDSADRESSE_PATH + ".vegadresse";
    private static final String B_MATRIKKELADRESSE_PATH = BOSTEDSADRESSE_PATH + ".matrikkeladresse";
    private static final String B_HISTORISK_PATH = BOSTEDSADRESSE_PATH + HISTORISK_PATH;

    public static void addAdresserQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addKommunenrBostedQuery(queryBuilder, search);
        addPostnrBostedQuery(queryBuilder, search);
    }

    private static void addKommunenrBostedQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .flatMap(value -> Optional.ofNullable(value.getBostedsadresse()))
                .flatMap(value -> Optional.ofNullable(value.getKommunenummer()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                BOSTEDSADRESSE_PATH,
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.matchQuery(B_VEGADRESSE_PATH + ".kommunenummer", value))
                                        .should(QueryBuilders.matchQuery(B_MATRIKKELADRESSE_PATH + ".kommunenummer", value))
                                        .must(QueryBuilders.termQuery(B_HISTORISK_PATH, false))
                                        .minimumShouldMatch(1)
                                ,
                                ScoreMode.Avg
                        ));
                    }
                });
    }

    private static void addPostnrBostedQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .flatMap(value -> Optional.ofNullable(value.getBostedsadresse()))
                .flatMap(value -> Optional.ofNullable(value.getPostnummer()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.nestedQuery(
                                BOSTEDSADRESSE_PATH,
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.matchQuery(B_VEGADRESSE_PATH + ".postnummer", value))
                                        .should(QueryBuilders.matchQuery(B_MATRIKKELADRESSE_PATH + ".postnummer", value))
                                        .must(QueryBuilders.termQuery(B_HISTORISK_PATH, false))
                                        .minimumShouldMatch(1)
                                ,
                                ScoreMode.Avg
                        ));
                    }
                });
    }
}

