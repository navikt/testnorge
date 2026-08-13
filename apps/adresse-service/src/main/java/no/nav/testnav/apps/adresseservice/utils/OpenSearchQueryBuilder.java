package no.nav.testnav.apps.adresseservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.FunctionBoostMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RandomScoreFunction;

import java.security.SecureRandom;
import java.util.Random;

import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addBruksnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addGaardsnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addKommunenummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addMatrikkelIdQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addMatrikkelQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addPostnummerQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addPoststedQuery;
import static no.nav.testnav.apps.adresseservice.utils.OpenSearchMatrikkeladresseQueryUtils.addTilleggsnavnQuery;
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

    public static FunctionScoreQuery.Builder buildSearchQuery(VegadresseRequest request) {

        return new FunctionScoreQuery.Builder()
                .functions(getRandomScoreQuery())
                .boostMode(FunctionBoostMode.Replace)
                .query(Query.builder()
                        .bool(getVegadresseQuery(request))
                        .build());
    }

    public static FunctionScoreQuery.Builder buildSearchQuery(MatrikkeladresseRequest request) {

        return new FunctionScoreQuery.Builder()
                .functions(getRandomScoreQuery())
                .boostMode(FunctionBoostMode.Replace)
                .query(Query.builder()
                        .bool(getMatrikkeladresseQuery(request))
                        .build());
    }

    private static BoolQuery getVegadresseQuery(VegadresseRequest request) {

        var queryBuilder = QueryBuilders.bool();

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

        return queryBuilder.build();
    }

    private static BoolQuery getMatrikkeladresseQuery(MatrikkeladresseRequest request) {

        var queryBuilder = QueryBuilders.bool();

        addMatrikkelQuery(queryBuilder);
        addMatrikkelIdQuery(queryBuilder, request);
        addTilleggsnavnQuery(queryBuilder, request);
        addKommunenummerQuery(queryBuilder, request);
        addGaardsnummerQuery(queryBuilder, request);
        addBruksnummerQuery(queryBuilder, request);
        addPostnummerQuery(queryBuilder, request);
        addPoststedQuery(queryBuilder, request);

        return queryBuilder.build();
    }

    private static FunctionScore getRandomScoreQuery() {

        return new FunctionScore.Builder()
                .randomScore(new RandomScoreFunction.Builder()
                        .seed(JsonData.of(SEED.nextInt()))
                        .field("_seq_no")
                        .build())
                .build();
    }
}
