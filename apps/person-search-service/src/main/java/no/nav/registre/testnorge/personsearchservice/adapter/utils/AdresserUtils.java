package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Arrays;
import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedBoolShouldQuery;

@UtilityClass
public class AdresserUtils {
    private static final String BOSTEDSADRESSE_PATH = "hentPerson.bostedsadresse";
    private static final String B_VEGADRESSE_PATH = BOSTEDSADRESSE_PATH + ".vegadresse";
    private static final String B_MATRIKKELADRESSE_PATH = BOSTEDSADRESSE_PATH + ".matrikkeladresse";

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
                        var fields = Arrays.asList(
                                new FieldQuery(B_VEGADRESSE_PATH + ".kommunenummer", value),
                                new FieldQuery(B_MATRIKKELADRESSE_PATH + ".kommunenummer", value));
                        queryBuilder.must(nestedBoolShouldQuery(BOSTEDSADRESSE_PATH, fields, 1, false));
                    }
                });
    }

    private static void addPostnrBostedQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .flatMap(value -> Optional.ofNullable(value.getBostedsadresse()))
                .flatMap(value -> Optional.ofNullable(value.getPostnummer()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        var fields = Arrays.asList(
                                new FieldQuery(B_VEGADRESSE_PATH + ".postnummer", value),
                                new FieldQuery(B_MATRIKKELADRESSE_PATH + ".postnummer", value));
                        queryBuilder.must(nestedBoolShouldQuery(BOSTEDSADRESSE_PATH, fields, 1, false));
                    }
                });
    }
}

