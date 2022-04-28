package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.AdresserSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.YES;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.NO;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.METADATA_FIELD;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldExistQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedShouldMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;

@UtilityClass
public class AdresserUtils {
    private static final String BOSTEDSADRESSE_PATH = "hentPerson.bostedsadresse";
    private static final String KONTAKTADRESSE_PATH = "hentPerson.kontaktadresse";
    private static final String OPPHOLDSADRESSE_PATH = "hentPerson.oppholdsadresse";

    public static void addAdresserQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAdresser())
                .ifPresent(adresser -> {
                    if (nonNull(adresser.getBostedsadresse())) {
                        addKommunenrBostedQuery(queryBuilder, adresser.getBostedsadresse());
                        addPostnrBostedQuery(queryBuilder, adresser.getBostedsadresse());
                        addBorINorgeQuery(queryBuilder, adresser.getBostedsadresse());
                    }
                    if (nonNull(adresser.getOppholdsadresse())) {
                        addOppholdAnnetStedQuery(queryBuilder, adresser.getOppholdsadresse());
                    }
                    addHarUtenlandskAdresseQuery(queryBuilder, adresser);
                    addHarKontaktadresseQuery(queryBuilder, adresser);
                    addHarOppholdsadresseQuery(queryBuilder, adresser);
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

    private static void addBorINorgeQuery(BoolQueryBuilder queryBuilder, AdresserSearch.BostedsadresseSearch bostedsadresse) {
        Optional.ofNullable(bostedsadresse.getBorINorge())
                .ifPresent(value -> {
                    var newQuery = nestedShouldExistQuery(
                            BOSTEDSADRESSE_PATH,
                            Arrays.asList(".vegadresse", ".matrikkeladresse", ".ukjentBosted"),
                            1,
                            false
                    );
                    if (YES.equalsIgnoreCase(value)) {
                        queryBuilder.must(newQuery);
                    }else if(NO.equalsIgnoreCase(value)){
                        queryBuilder.mustNot(newQuery);
                    }
                });
    }

    private static void addHarKontaktadresseQuery(BoolQueryBuilder queryBuilder, AdresserSearch search ) {
        Optional.ofNullable(search.getHarKontaktadresse())
                .ifPresent(value -> {
                    if (YES.equalsIgnoreCase(value)) {
                        queryBuilder.must(nestedExistsQuery(KONTAKTADRESSE_PATH, METADATA_FIELD, false));
                    }else if(NO.equalsIgnoreCase(value)){
                        queryBuilder.mustNot(nestedExistsQuery(KONTAKTADRESSE_PATH, METADATA_FIELD, false));
                    }
                });
    }

    private static void addHarOppholdsadresseQuery(BoolQueryBuilder queryBuilder, AdresserSearch search ) {
        Optional.ofNullable(search.getHarOppholdsadresse())
                .ifPresent(value -> {
                    if (YES.equalsIgnoreCase(value)) {
                        queryBuilder.must(nestedExistsQuery(OPPHOLDSADRESSE_PATH, METADATA_FIELD, false));
                    }else if(NO.equalsIgnoreCase(value)){
                        queryBuilder.mustNot(nestedExistsQuery(OPPHOLDSADRESSE_PATH, METADATA_FIELD, false));
                    }
                });
    }

    private static void addHarUtenlandskAdresseQuery(BoolQueryBuilder queryBuilder, AdresserSearch search ) {
        Optional.ofNullable(search.getHarUtenlandskAdresse())
                .ifPresent(value -> {
                    var newQuery = QueryBuilders.boolQuery()
                            .should(nestedExistsQuery(OPPHOLDSADRESSE_PATH, ".utenlandskAdresse", false))
                            .should(nestedExistsQuery(KONTAKTADRESSE_PATH, ".utenlandskAdresse", false))
                            .minimumShouldMatch(1);
                    if (YES.equalsIgnoreCase(value)) {
                        queryBuilder.must(newQuery);
                    }else if(NO.equalsIgnoreCase(value)){
                        queryBuilder.mustNot(newQuery);
                    }
                });
    }

    private static void addOppholdAnnetStedQuery(BoolQueryBuilder queryBuilder, AdresserSearch.OppholdsadresseSearch oppholdsadresse) {
        Optional.ofNullable(oppholdsadresse.getOppholdAnnetSted())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(OPPHOLDSADRESSE_PATH, ".oppholdAnnetSted", value, false));
                    }
                });
    }
}

