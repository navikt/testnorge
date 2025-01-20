package no.nav.testnav.dollysearchservice.service;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseKommunenrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdressebeskyttelseQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAlderQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseBydelsnrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseMatrikkelQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdressePostnrQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addBostedUkjentQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addAdresseUtlandQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addDoedsfallQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addDollyIdentifier;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarBarnQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addHarAdresseBydelsnrQuery;
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
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addSivilstandQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addStatsborgerskapQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchPersonQueryUtils.addVergemaalQuery;

@UtilityClass
public class OpenSearchQueryBuilder {

    private static final Random seed = new SecureRandom();

    public static BoolQueryBuilder buildSearchQuery(SearchRequest request) {

        var queryBuilder = buildTyperQuery(request.getTyper().toArray(ElasticTyper[]::new));
        setPersonQuery(queryBuilder, request);

        return queryBuilder;
    }

    public static BoolQueryBuilder buildTyperQuery(ElasticTyper[] typer) {

        var queryBuilder = QueryBuilders.boolQuery()
                .must(getRandomScoreQueryBuilder())
                .must(addDollyIdentifier());

//        Arrays.stream(typer)
//                .map(OpenSearchQueryBuilder::getFagsystemQuery)
//                .forEach(queryBuilder::must);

        return queryBuilder;
    }

    private void setPersonQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

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
                    addHarAdresseBydelsnrQuery(queryBuilder, request);
                    addAdresseUtlandQuery(queryBuilder, request);
                    addAdresseMatrikkelQuery(queryBuilder, request);
                    addBostedUkjentQuery(queryBuilder, request);
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
                });
    }

    private QueryBuilder getFagsystemQuery(ElasticTyper type) {

        return switch (type) {
            case AAREG -> QueryBuilders.existsQuery("aareg");
            case INST -> QueryBuilders.existsQuery("instdata");
            case KRRSTUB -> QueryBuilders.existsQuery("krrstub");
            case SIGRUN_LIGNET -> QueryBuilders.existsQuery("sigrunstub");
            case SIGRUN_PENSJONSGIVENDE -> QueryBuilders.existsQuery("sigrunstubPensjonsgivende");
            case ARENA_AAP -> QueryBuilders.existsQuery("arenaforvalter.aap");
            case ARENA_AAP115 -> QueryBuilders.existsQuery("arenaforvalter.aap115");
            case ARENA_DAGP -> QueryBuilders.existsQuery("arenaforvalter.dagpenger");
            case UDISTUB -> QueryBuilders.existsQuery("udistub");
            case INNTK -> QueryBuilders.existsQuery("inntektstub");
            case PEN_INNTEKT -> QueryBuilders.existsQuery("pensjonforvalter.inntekt");
            case PEN_TP -> QueryBuilders.existsQuery("pensjonforvalter.tp");
            case PEN_AP -> QueryBuilders.existsQuery("pensjonforvalter.alderspensjon");
            case PEN_UT -> QueryBuilders.existsQuery("pensjonforvalter.uforetrygd");
            case PEN_AFP_OFFENTLIG -> QueryBuilders.existsQuery("pensjonforvalter.afpOffentlig");
            case PEN_PENSJONSAVTALE -> QueryBuilders.existsQuery("pensjonforvalter.pensjonsavtale");
            case INNTKMELD -> QueryBuilders.existsQuery("inntektsmelding");
            case BRREGSTUB -> QueryBuilders.existsQuery("brregstub");
            case DOKARKIV -> QueryBuilders.existsQuery("dokarkiv");
            case FULLMAKT -> QueryBuilders.existsQuery("fullmakt");
            case MEDL -> QueryBuilders.existsQuery("medl");
            case HISTARK -> QueryBuilders.existsQuery("histark");
            case SYKEMELDING -> QueryBuilders.existsQuery("sykemelding");
            case SKJERMING -> QueryBuilders.existsQuery("skjerming");
            case BANKKONTO -> QueryBuilders.existsQuery("bankkonto");
            case BANKKONTO_NORGE -> QueryBuilders.existsQuery("bankkonto.norskBankkonto");
            case BANKKONTO_UTLAND -> QueryBuilders.existsQuery("bankkonto.utenlandskBankkonto");
            case ARBEIDSPLASSENCV -> QueryBuilders.existsQuery("arbeidsplassenCV");
            case SKATTEKORT -> QueryBuilders.existsQuery("skattekort");
            case YRKESSKADE -> QueryBuilders.existsQuery("yrkesskader");
        };
    }

    private static FunctionScoreQueryBuilder getRandomScoreQueryBuilder() {

        return QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder().seed(seed.nextInt()));
    }
}
