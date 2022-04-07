package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.AdresserSearch;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Arrays;
import java.util.Optional;

import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldExistQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.NO;

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
                        addUtenlandskKontaktadresseQuery(queryBuilder, adresser.getKontaktadresse());
                    }
                });
    }

    private static void addKommunenrBostedQuery(BoolQueryBuilder queryBuilder, AdresserSearch.BostedsadresseSearch bostedsadresse) {
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

    private static void addPostnrBostedQuery(BoolQueryBuilder queryBuilder, AdresserSearch.BostedsadresseSearch bostedsadresse) {
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


    private static void addUtenlandskKontaktadresseQuery(BoolQueryBuilder queryBuilder, AdresserSearch.KontaktadresseSearch kontaktadresse) {
        Optional.ofNullable(kontaktadresse.getNorskAdresse())
                .ifPresent(value -> {
                    if (NO.equalsIgnoreCase(value)) {
                        queryBuilder.must(nestedShouldExistQuery(
                                KONTAKTADRESSE_PATH,
                                Arrays.asList(".utenlandskAdresse.landkode", ".utenlandskAdresseIFrittFormat.landkode"),
                                1,
                                false
                        ));
                    } else if (YES.equalsIgnoreCase(value)) {
                        queryBuilder.must(nestedShouldExistQuery(
                                KONTAKTADRESSE_PATH,
                                Arrays.asList(".vegadresse.postnummer", ".postboksadresse.postnummer", ".postadresseIFrittFormat.postnummer"),
                                1,
                                false
                        ));
                    }
                });
    }

}

