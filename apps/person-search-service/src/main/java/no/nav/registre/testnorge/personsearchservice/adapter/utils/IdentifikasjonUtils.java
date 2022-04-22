package no.nav.registre.testnorge.personsearchservice.adapter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.personsearchservice.controller.search.IdentifikasjonSearch;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedMatchQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedExistsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.nestedTermsQuery;
import static no.nav.registre.testnorge.personsearchservice.adapter.utils.QueryUtils.METADATA_FIELD;

@UtilityClass
public class IdentifikasjonUtils {

    private static final String NO_VALUE = "INGEN";
    private static final String ADRESSEBESKYTTELSE_PATH = "hentPerson.adressebeskyttelse";
    private static final String KJOENN_PATH = "hentPerson.kjoenn";
    private static final String IDENTIFIKATOR_PATH = "hentPerson.folkeregisteridentifikator";

    public static void addIdentifikasjonQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addIdentQuery(queryBuilder, search);
        addKjoennQuery(queryBuilder, search);

        Optional.ofNullable(search.getIdentifikasjon())
                .ifPresent(value -> {
                    addIdenttypeQuery(queryBuilder, value);
                    addIdentitetQueries(queryBuilder, value);
                    addAdressebeskyttelseQuery(queryBuilder, value);
                });
    }

    private static void addIdenttypeQuery(BoolQueryBuilder queryBuilder, IdentifikasjonSearch search) {
        Optional.ofNullable(search.getIdenttype())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(IDENTIFIKATOR_PATH, ".type", value, false));
                    }
                });
    }

    private static void addIdentitetQueries(BoolQueryBuilder queryBuilder, IdentifikasjonSearch search) {
        if (nonNull(search.getFalskIdentitet()) && search.getFalskIdentitet()) {
            queryBuilder.must(nestedExistsQuery("hentPerson.falskIdentitet", METADATA_FIELD, true));
        }
        if (nonNull(search.getUtenlandskIdentitet()) && search.getUtenlandskIdentitet()) {
            queryBuilder.must(nestedExistsQuery("hentPerson.utenlandskIdentifikasjonsnummer", METADATA_FIELD, true));
        }
    }

    private static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, IdentifikasjonSearch search) {
        Optional.ofNullable(search.getAdressebeskyttelse())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        if (value.equals(NO_VALUE)) {
                            queryBuilder.mustNot(nestedExistsQuery(ADRESSEBESKYTTELSE_PATH, METADATA_FIELD, true));
                        } else {
                            queryBuilder.must(nestedMatchQuery(ADRESSEBESKYTTELSE_PATH, ".gradering", value, false));
                        }
                    }
                });
    }

    private static void addKjoennQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKjoenn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(nestedMatchQuery(KJOENN_PATH, ".kjoenn", value, false));
                    }
                });
    }

    private static void addIdentQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdenter())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(nestedTermsQuery("hentIdenter.identer", ".ident", values, true));
                    }
                });
    }

}
