package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;

@UtilityClass
public class StatusUtils {

    private static final String PERSONSTATUS_PATH = "hentPerson.folkeregisterpersonstatus";
    private static final String DOEDSFALL_PATH = "hentPerson.doedsfall";

    public static void addStatusQueries(BoolQueryBuilder queryBuilder, PersonSearch search){
        addPersonstatusQuery(queryBuilder, search);
        addLevendeQuery(queryBuilder, search);
    }

    private static void addPersonstatusQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getPersonstatus())
                .flatMap(value -> Optional.ofNullable(value.getStatus()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(PERSONSTATUS_PATH, ".status", value, false));
                    }
                });
    }

    private static void addLevendeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKunLevende())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.mustNot(nestedExistsQuery(DOEDSFALL_PATH, ".doedsdato", true));
                    }
                });
    }

}
