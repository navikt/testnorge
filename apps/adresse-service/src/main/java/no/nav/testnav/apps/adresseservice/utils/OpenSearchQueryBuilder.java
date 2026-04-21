package no.nav.testnav.apps.adresseservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RandomScoreFunction;

import java.security.SecureRandom;
import java.util.Random;

import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addBruksnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addGaardsnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addMatrikkelQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addAdressenavnQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addBydelsnavnQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addBydelsnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addFritekstQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addHusbokstavQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addHusnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addKommunenavnQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addKommunenummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addMatrikkelIdQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addPostnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addPoststedQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchVegadresseQueryUtils.addTilleggsnavnQuery;

@UtilityClass
public class OpenSearchQueryBuilder {

    private static final Random SEED = new SecureRandom();

    public static BoolQuery.Builder buildSearchQuery(VegadresseRequest request) {

        var queryBuilder = QueryBuilders.bool()
                .must(q -> q.functionScore(getRandomScoreQueryBuilder()));

        setVegadresseQuery(queryBuilder, request);

        return queryBuilder;
    }

    public static BoolQuery.Builder buildSearchQuery(MatrikkeladresseRequest request) {

        var queryBuilder = QueryBuilders.bool()
                .must(q -> q.functionScore(getRandomScoreQueryBuilder()));

        setMatrikkeladresseQuery(queryBuilder, request);

        return queryBuilder;
    }

    private static void setVegadresseQuery(BoolQuery.Builder queryBuilder, VegadresseRequest request) {

        addMatrikkelIdQuery(queryBuilder, request);
        addAdressenavnQuery(queryBuilder, request);
        addHusnummerQuery(queryBuilder, request);
        addHusbokstavQuery(queryBuilder, request);
        addPostnummerQuery(queryBuilder, request);
        addPoststedQuery(queryBuilder, request);
        addKommunenavnQuery(queryBuilder, request);
        addKommunenummerQuery(queryBuilder, request);
        addBydelsnummerQuery(queryBuilder, request);
        addBydelsnavnQuery(queryBuilder, request);
        addTilleggsnavnQuery(queryBuilder, request);
        addFritekstQuery(queryBuilder, request);
    }

    private static void setMatrikkeladresseQuery(BoolQuery.Builder queryBuilder, MatrikkeladresseRequest request) {

        addMatrikkelQuery(queryBuilder);
        OpenSearchMatrikkeladresseQueryUtils.addMatrikkelIdQuery(queryBuilder, request);
        OpenSearchMatrikkeladresseQueryUtils.addTilleggsnavnQuery(queryBuilder, request);
        OpenSearchMatrikkeladresseQueryUtils.addKommunenummerQuery(queryBuilder, request);
        addGaardsnummerQuery(queryBuilder, request);
        addBruksnummerQuery(queryBuilder, request);
        OpenSearchMatrikkeladresseQueryUtils.addPostnummerQuery(queryBuilder, request);
        OpenSearchMatrikkeladresseQueryUtils.addPoststedQuery(queryBuilder, request);
    }

    private static FunctionScoreQuery getRandomScoreQueryBuilder() {

        return QueryBuilders.functionScore()
                .functions(q1 -> q1.randomScore(
                        RandomScoreFunction.of(q2 -> q2.seed(Integer.toString(SEED.nextInt())))))
                .build();
    }
}
