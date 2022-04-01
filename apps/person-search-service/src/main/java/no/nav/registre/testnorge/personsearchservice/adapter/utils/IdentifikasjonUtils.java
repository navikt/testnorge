package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;

@UtilityClass
public class IdentifikasjonUtils {

    public static void addIdentifikasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search){
        addIdenttypeQuery(queryBuilder, search);
        addIdentitetQueries(queryBuilder, search);
        addAdressebeskyttelseQuery(queryBuilder, search);
        addKjoennQuery(queryBuilder, search);
    }

    private static void addIdenttypeQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .flatMap(value -> Optional.ofNullable(value.getIdenttype()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.folkeregisteridentifikator", "type", value));
                    }
                });
    }

    private static void addIdentitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .ifPresent(value -> {
                    if (nonNull(value.getFalskIdentitet()) && value.getFalskIdentitet()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.falskIdentitet", "metadata"));
                    }
                    if (nonNull(value.getUtenlandskIdentitet()) && value.getUtenlandskIdentitet()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.utenlandskIdentifikasjonsnummer", "metadata"));
                    }

                });
    }

    private static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .flatMap(value -> Optional.ofNullable(value.getAdressebeskyttelse()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.adressebeskyttelse", "gradering", value));
                    }
                });
    }

    private static void addKjoennQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKjoenn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery("hentPerson.kjoenn", "kjoenn", value));
                    }
                });
    }

}
