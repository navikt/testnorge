package no.nav.testnav.apps.adresseservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;

import static no.nav.testnav.apps.adresseservice.utils.OpenSearchQueryUtils.fuzzyQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchQueryUtils.matchQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchVegadresseQueryUtils {

    public static void addMatrikkelIdQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getMatrikkelId())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.id", request.getMatrikkelId())));
        }
    }

    public static void addAdressenavnQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getAdressenavn())) {

            queryBuilder.must(q -> q.fuzzy(fuzzyQuery("vegadresse.veg.adressenavn", request.getAdressenavn())));
        }
    }

    public static void addHusnummerQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getHusnummer())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.nummer", request.getHusnummer())));
        }
    }

    public static void addHusbokstavQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getHusbokstav())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.bokstav", request.getHusbokstav())));
        }
    }

    public static void addPostnummerQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getPostnummer())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.postnummeromraade.postnummer", request.getPostnummer())));
        }
    }

    public static void addPoststedQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getPoststed())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.postnummeromraade.poststed", request.getPoststed())));
        }
    }

    public static void addKommunenavnQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getKommunenavn())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.veg.kommune.kommunenavn", request.getKommunenavn())));
        }
    }

    public static void addKommunenummerQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getKommunenummer())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.veg.kommune.kommunenummer", request.getKommunenummer())));
        }
    }

    public static void addBydelsnummerQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getBydelsnummer())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.bydel.bydelsnummer", request.getBydelsnummer())));
        }
    }

    public static void addBydelsnavnQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getBydelsnavn())) {

            queryBuilder.must(q -> q.match(matchQuery("vegadresse.bydel.bydelsnavn", request.getBydelsnavn())));
        }
    }

    public static void addTilleggsnavnQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getTilleggsnavn())) {

            queryBuilder.must(q -> q.fuzzy(fuzzyQuery("vegadresse.adressetilleggsnavn", request.getTilleggsnavn())));
        }
    }

    public static void addFritekstQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        if (isNotBlank(request.getFritekst())) {

            queryBuilder.must(q -> q.fuzzy(fuzzyQuery("custom_fields.fritekst.vegadresse", request.getFritekst())));
        }
    }
}
