package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.BostedsadresseSearch;
import no.nav.registre.testnorge.personsearchservice.controller.search.KontaktadresseSearch;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Arrays;
import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldExistQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;

import static java.util.Objects.nonNull;

@UtilityClass
public class AdresserUtils {
    private static final String BOSTEDSADRESSE_PATH = "hentPerson.bostedsadresse";
    private static final String KONTAKTADRESSE_PATH = "hentPerson.kontaktadresse";

    public static void addAdresserQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .ifPresent(adresser -> {
                    if (nonNull(adresser.getBostedsadresse())) {
                        addKommunenrBostedQuery(queryBuilder, adresser.getBostedsadresse());
                        addPostnrBostedQuery(queryBuilder, adresser.getBostedsadresse());
                    }
                    if (nonNull(adresser.getKontaktadresse())) {
                        addKommunenrKontaktQuery(queryBuilder, adresser.getKontaktadresse());
                        addPostnrKontaktQuery(queryBuilder, adresser.getKontaktadresse());
                        addUtenlandskKontaktadresseQuery(queryBuilder, adresser.getKontaktadresse());
                    }
                });
    }

    private static void addKommunenrBostedQuery(BoolQueryBuilder queryBuilder, BostedsadresseSearch bostedsadresse) {
        Optional.ofNullable(bostedsadresse.getKommunenummer())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedShouldMatchQuery(
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
                        queryBuilder.must(nestedShouldMatchQuery(
                                BOSTEDSADRESSE_PATH,
                                Arrays.asList(".vegadresse.postnummer", ".matrikkeladresse.postnummer"),
                                value, 1, false));
                    }
                });
    }

    private static void addKommunenrKontaktQuery(BoolQueryBuilder queryBuilder, KontaktadresseSearch kontaktadresse) {
        Optional.ofNullable(kontaktadresse.getKommunenummer())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(KONTAKTADRESSE_PATH, ".vegadresse.kommunenummer", value, false));
                    }
                });
    }

    private static void addPostnrKontaktQuery(BoolQueryBuilder queryBuilder, KontaktadresseSearch kontaktadresse) {
        Optional.ofNullable(kontaktadresse.getPostnummer())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedShouldMatchQuery(
                                KONTAKTADRESSE_PATH,
                                Arrays.asList(".vegadresse.postnummer", ".postboksadresse.postnummer", ".postadresseIFrittFormat.postnummer"),
                                value, 1, false));
                    }
                });
    }

    private static void addUtenlandskKontaktadresseQuery(BoolQueryBuilder queryBuilder, KontaktadresseSearch kontaktadresse) {
        Optional.ofNullable(kontaktadresse.getUtenlandskAdresse())
                .ifPresent(value -> {
                    if (Boolean.TRUE.equals(value)) {
                        queryBuilder.must(nestedShouldExistQuery(
                                KONTAKTADRESSE_PATH,
                                Arrays.asList(".utenlandskAdresse.landkode", ".utenlandskAdresseIFrittFormat.landkode"),
                                1,
                                false
                        ));
                    }
                });
    }

}

