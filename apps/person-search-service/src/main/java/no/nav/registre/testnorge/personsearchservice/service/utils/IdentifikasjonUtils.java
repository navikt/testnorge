package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.IdentifikasjonSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Optional;

import static java.util.Objects.nonNull;

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
                        queryBuilder.must(QueryUtils.nestedMatchQuery(IDENTIFIKATOR_PATH, ".type", value, false));
                    }
                });
    }

    private static void addIdentitetQueries(BoolQueryBuilder queryBuilder, IdentifikasjonSearch search) {
        if (nonNull(search.getFalskIdentitet()) && search.getFalskIdentitet()) {
            queryBuilder.must(QueryUtils.nestedExistsQuery("hentPerson.falskIdentitet", QueryUtils.METADATA_FIELD, true));
        }
        if (nonNull(search.getUtenlandskIdentitet()) && search.getUtenlandskIdentitet()) {
            queryBuilder.must(QueryUtils.nestedExistsQuery("hentPerson.utenlandskIdentifikasjonsnummer", QueryUtils.METADATA_FIELD, true));
        }
    }

    private static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, IdentifikasjonSearch search) {
        Optional.ofNullable(search.getAdressebeskyttelse())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        if (value.equals(NO_VALUE)) {
                            queryBuilder.mustNot(QueryUtils.nestedExistsQuery(ADRESSEBESKYTTELSE_PATH, QueryUtils.METADATA_FIELD, true));
                        } else {
                            queryBuilder.must(QueryUtils.nestedMatchQuery(ADRESSEBESKYTTELSE_PATH, ".gradering", value, false));
                        }
                    }
                });
    }

    private static void addKjoennQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getKjoenn())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryUtils.nestedMatchQuery(KJOENN_PATH, ".kjoenn", value, false));
                    }
                });
    }

    private static void addIdentQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getIdenter())
                .ifPresent(values -> {
                    if (!values.isEmpty()) {
                        queryBuilder.must(QueryUtils.nestedTermsQuery("hentIdenter.identer", ".ident", values, true));
                    }
                });
    }

}
