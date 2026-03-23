package no.nav.testnav.apps.adresseservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;

import static no.nav.testnav.apps.adresseservice.utils.OpenSearchQueryUtils.existQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchQueryUtils.matchQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchMatrikkeladresseQueryUtils {

    public static void addMatrikkelQuery(BoolQuery.Builder queryBuilder) {

            queryBuilder.must(q -> q.exists(existQuery("matrikkeladresse")));
    }

    public static void addMatrikkelIdQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getMatrikkelId())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.id", request.getMatrikkelId())));
        }
    }

    public static void addTilleggsnavnQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getTilleggsnavn())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.adressetilleggsnavn", request.getTilleggsnavn())));
        }
    }

    public static void addKommunenummerQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getKommunenummer())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.matrikkelenhet.matrikkelnummer.kommunenummer", request.getKommunenummer())));
        }
    }

    public static void addGaardsnummerQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getGaardsnummer())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.matrikkelenhet.matrikkelnummer.gardsnummer", request.getGaardsnummer())));
        }
    }

    public static void addBruksnummerQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getBrukesnummer())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.matrikkelenhet.matrikkelnummer.bruksnummer", request.getBrukesnummer())));
        }
    }

    public static void addPostnummerQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getPostnummer())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.postnummeromraade.postnummer", request.getPostnummer())));
        }
    }

    public static void addPoststedQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        if (isNotBlank(request.getPoststed())) {

            queryBuilder.must(q -> q.match(matchQuery("matrikkeladresse.postnummeromraade.poststed", request.getPoststed())));
        }
    }
}
