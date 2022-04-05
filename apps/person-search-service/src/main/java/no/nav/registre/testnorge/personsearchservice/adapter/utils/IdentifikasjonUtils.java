package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedHistoriskQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedTermsQuery;

@UtilityClass
public class IdentifikasjonUtils {

    private static final String NO_VALUE = "INGEN";
    private static final String METADATA = "metadata";
    private static final String ADRESSEBESKYTTELSE_PATH = "hentPerson.adressebeskyttelse";
    private static final String KJOENN_PATH = "hentPerson.kjoenn";
    private static final String IDENTIFIKATOR_PATH = "hentPerson.folkeregisteridentifikator";

    public static void addIdentifikasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addIdentQuery(queryBuilder, search);
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
                        queryBuilder.must(nestedHistoriskQuery(IDENTIFIKATOR_PATH, "type", value, false));
                    }
                });
    }

    private static void addIdentitetQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .ifPresent(value -> {
                    if (nonNull(value.getFalskIdentitet()) && value.getFalskIdentitet()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.falskIdentitet", METADATA));
                    }
                    if (nonNull(value.getUtenlandskIdentitet()) && value.getUtenlandskIdentitet()) {
                        queryBuilder.must(nestedExistsQuery("hentPerson.utenlandskIdentifikasjonsnummer", METADATA));
                    }

                });
    }

    private static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdentifikasjon())
                .flatMap(value -> Optional.ofNullable(value.getAdressebeskyttelse()))
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        if (value.equals(NO_VALUE)) {
                            queryBuilder.mustNot(nestedExistsQuery(ADRESSEBESKYTTELSE_PATH, METADATA));
                        } else {
                            queryBuilder.must(nestedHistoriskQuery(ADRESSEBESKYTTELSE_PATH, "gradering", value, false));
                        }
                    }
                });
    }

    private static void addKjoennQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKjoenn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedHistoriskQuery(KJOENN_PATH, "kjoenn", value, false));
                    }
                });
    }

    private static void addIdentQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdenter())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(nestedTermsQuery("hentIdenter.identer", "ident", values));
                    }
                });
    }

}
