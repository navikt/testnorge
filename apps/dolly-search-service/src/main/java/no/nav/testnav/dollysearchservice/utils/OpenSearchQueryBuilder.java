package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RandomScoreFunction;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import static java.util.Objects.isNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseBydelsnrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseKommunenrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseMatrikkelQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdressePostnrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseUtlandQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdressebeskyttelseQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAlderQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addDoedsfallQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarAdresseBydelsnummerQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarBarnQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarBostedUkjentQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarBostedsadresseQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarDeltBostedQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarDoedfoedtbarnQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarFalskIdentitetQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarForeldreQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarForeldreansvarQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarInnflyttingQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarKontaktadresseQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarKontaktinformasjonForDoedsboQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarNyIdentitetQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarOppholdQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarOppholdsadresseQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarSikkerhetstiltakQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarTilrettelagtKommunikasjonQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarUtenlandskIdentifikasjonsnummerQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarUtflyttingQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addIdenttypeQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addKjoennQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addKunLevendePersonerQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addPersonStatusQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addSivilstandQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addStatsborgerskapQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addVergemaalQuery;

@UtilityClass
public class OpenSearchQueryBuilder {

    private static final Random SEED = new SecureRandom();

    public static BoolQuery.Builder buildSearchQuery(SearchRequest request) {

        var queryBuilder = QueryBuilders.bool()
                .must(q -> q.functionScore(getRandomScoreQueryBuilder(request)));

        setPersonQuery(queryBuilder, request);

        return queryBuilder;
    }

    private static void setPersonQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest())
                .ifPresent(value -> {

                    addAlderQuery(queryBuilder, request);
                    addHarBarnQuery(queryBuilder, request);
                    addHarForeldreQuery(queryBuilder, request);
                    addSivilstandQuery(queryBuilder, request);
                    addHarDoedfoedtbarnQuery(queryBuilder, request);
                    addHarForeldreansvarQuery(queryBuilder, request);
                    addVergemaalQuery(queryBuilder, request);
                    addDoedsfallQuery(queryBuilder, request);
                    addHarInnflyttingQuery(queryBuilder, request);
                    addHarUtflyttingQuery(queryBuilder, request);
                    addAdressebeskyttelseQuery(queryBuilder, request);
                    addHarBostedsadresseQuery(queryBuilder, request);
                    addHarOppholdsadresseQuery(queryBuilder, request);
                    addHarKontaktadresseQuery(queryBuilder, request);
                    addAdressebeskyttelseQuery(queryBuilder, request);
                    addAdresseKommunenrQuery(queryBuilder, request);
                    addAdressePostnrQuery(queryBuilder, request);
                    addAdresseBydelsnrQuery(queryBuilder, request);
                    addHarAdresseBydelsnummerQuery(queryBuilder, request);
                    addAdresseUtlandQuery(queryBuilder, request);
                    addAdresseMatrikkelQuery(queryBuilder, request);
                    addHarBostedUkjentQuery(queryBuilder, request);
                    addHarDeltBostedQuery(queryBuilder, request);
                    addHarKontaktinformasjonForDoedsboQuery(queryBuilder, request);
                    addHarUtenlandskIdentifikasjonsnummerQuery(queryBuilder, request);
                    addHarFalskIdentitetQuery(queryBuilder, request);
                    addHarTilrettelagtKommunikasjonQuery(queryBuilder, request);
                    addHarSikkerhetstiltakQuery(queryBuilder, request);
                    addStatsborgerskapQuery(queryBuilder, request);
                    addHarOppholdQuery(queryBuilder, request);
                    addHarNyIdentitetQuery(queryBuilder, request);
                    addKjoennQuery(queryBuilder, request);
                    addIdenttypeQuery(queryBuilder, request);
                    addPersonStatusQuery(queryBuilder, request);
                    addKunLevendePersonerQuery(queryBuilder, request);
                });
    }

    private static FunctionScoreQuery getRandomScoreQueryBuilder(SearchRequest request) {

        if (isNull(request.getSeed())){
            request.setSeed(SEED.nextInt());
        }

        return QueryBuilders.functionScore()
                .functions(q1 -> q1.randomScore(
                        RandomScoreFunction.of(q2 -> q2.seed(request.getSeed().toString()))))
                .build();
    }
}
