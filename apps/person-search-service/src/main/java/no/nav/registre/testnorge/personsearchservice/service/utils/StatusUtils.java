package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.opensearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.service.utils.QueryUtils.nestedMatchQuery;

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
                        queryBuilder.mustNot(nestedExistsQuery(DOEDSFALL_PATH, ".doedsdato", false));
                    }
                });
    }

}
