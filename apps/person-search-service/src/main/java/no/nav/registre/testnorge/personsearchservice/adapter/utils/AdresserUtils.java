package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.BostedsadresseSearch;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Arrays;
import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldQuery;

@UtilityClass
public class AdresserUtils {
    private static final String BOSTEDSADRESSE_PATH = "hentPerson.bostedsadresse";

    public static void addAdresserQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .flatMap(value -> Optional.ofNullable(value.getBostedsadresse()))
                .ifPresent(value -> {
                    addKommunenrBostedQuery(queryBuilder, value);
                    addPostnrBostedQuery(queryBuilder, value);
                    addUkjentBostedQuery(queryBuilder, value);
                    addUtenlandskBostedQuery(queryBuilder, value);
                });
    }

    private static void addKommunenrBostedQuery(BoolQueryBuilder queryBuilder, BostedsadresseSearch bostedsadresse) {
        Optional.ofNullable(bostedsadresse.getKommunenummer())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedShouldQuery(
                                BOSTEDSADRESSE_PATH,
                                Arrays.asList(".vegadresse.kommunenummer", ".matrikkeladresse.kommunenummer"),
                                value, 1, false));
                    }
                });
    }

    private static void addPostnrBostedQuery(BoolQueryBuilder queryBuilder, BostedsadresseSearch bostedsadresse) {
        Optional.ofNullable(bostedsadresse.getPostnummer())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedShouldQuery(
                                BOSTEDSADRESSE_PATH,
                                Arrays.asList(".vegadresse.postnummer", ".matrikkeladresse.postnummer"),
                                value, 1, false));
                    }
                });
    }

    private static void addUkjentBostedQuery(BoolQueryBuilder queryBuilder, BostedsadresseSearch bostedsadresse) {
        Optional.ofNullable(bostedsadresse.getUkjentBosted())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery(BOSTEDSADRESSE_PATH, ".ukjentBosted.bostedskommune", false));
                    }
                });
    }

    private static void addUtenlandskBostedQuery(BoolQueryBuilder queryBuilder, BostedsadresseSearch bostedsadresse) {
        Optional.ofNullable(bostedsadresse.getUtenlandskBosted())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedExistsQuery(BOSTEDSADRESSE_PATH, ".utenlandskAdresse.landkode", false));
                    }
                });
    }


}

