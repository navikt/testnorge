package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.FunctionBoostMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.RandomScoreFunction;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import static java.util.Objects.isNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchIdenterQueryUtils.addIdenterQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseBydelsnrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseKommunenrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseMatrikkelQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdressePostnrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseUtlandQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdressebeskyttelseQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAlderQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAntallBostedsadresserQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAntallKontaktadresserQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAntallOppholdsadresserQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAntallRelasjonerQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAntallStatsborgerskapQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addDoedsfallQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addFoedselsdatoQuery;
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
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.termsQuery;

@UtilityClass
public class OpenSearchQueryBuilder {

    private static final Random RANDOM = new SecureRandom();

    public static FunctionScoreQuery.Builder buildSearchQuery(SearchRequest request) {

        var queryBuilder = QueryBuilders.bool();
        setPersonQuery(queryBuilder, request);
        addIdenterQuery(queryBuilder, request.getIdenter());
        tagsMustQuery(queryBuilder, request);
        tagsMustNotQuery(queryBuilder, request);

        return new FunctionScoreQuery.Builder()
                .functions(getRandomScore(request))
                .boostMode(FunctionBoostMode.Replace)
                .query(Query.builder()
                        .bool(queryBuilder.build())
                        .build());
    }

    private static void setPersonQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest())
                .ifPresent(_ -> {

                    addAlderQuery(queryBuilder, request);
                    addFoedselsdatoQuery(queryBuilder, request);
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
                    addAntallBostedsadresserQuery(queryBuilder, request);
                    addAntallKontaktadresserQuery(queryBuilder, request);
                    addAntallOppholdsadresserQuery(queryBuilder, request);
                    addHarKontaktinformasjonForDoedsboQuery(queryBuilder, request);
                    addHarUtenlandskIdentifikasjonsnummerQuery(queryBuilder, request);
                    addHarFalskIdentitetQuery(queryBuilder, request);
                    addHarTilrettelagtKommunikasjonQuery(queryBuilder, request);
                    addHarSikkerhetstiltakQuery(queryBuilder, request);
                    addStatsborgerskapQuery(queryBuilder, request);
                    addAntallStatsborgerskapQuery(queryBuilder, request);
                    addAntallRelasjonerQuery(queryBuilder, request);
                    addHarOppholdQuery(queryBuilder, request);
                    addHarNyIdentitetQuery(queryBuilder, request);
                    addKjoennQuery(queryBuilder, request);
                    addIdenttypeQuery(queryBuilder, request);
                    addPersonStatusQuery(queryBuilder, request);
                    addKunLevendePersonerQuery(queryBuilder, request);
                });
    }

    private static FunctionScore getRandomScore(SearchRequest request) {

        if (isNull(request.getSeed())) {
            request.setSeed(RANDOM.nextInt());
        }

        return new FunctionScore.Builder()
                .randomScore(new RandomScoreFunction.Builder()
                        .seed(JsonData.of(request.getSeed()))  // Keeps the random order consistent for a single user/session
                        .field("_seq_no")     // Recommended unique field to avoid document duplicates within shards
                        .build())
                .build();
    }

    private static void tagsMustQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (!request.getMustHaveTags().isEmpty()) {
            queryBuilder.must(q -> q.match(matchQuery("tags", request.getMustHaveTags())));
        }
    }

    private static void tagsMustNotQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (!request.getMustNotHaveTags().isEmpty()) {
            queryBuilder.mustNot(q -> q.terms(termsQuery("tags", request.getMustNotHaveTags())));
        }
    }
}
