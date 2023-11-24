package no.nav.dolly.elastic.service;

import lombok.experimental.UtilityClass;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addAdressebeskyttelseQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBarnQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBostedBydelsnrQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBostedKommuneQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBostedMatrikkelQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBostedPostnrQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBostedUkjentQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addBostedUtlandQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addDoedsfallQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addForeldreQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addFullmaktQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarBostedBydelsnrQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarDeltBostedQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarDoedfoedtbarnQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarFalskIdentitetQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarForeldreansvarQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarInnflyttingQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarKontaktadresseQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarKontaktinformasjonForDoedsboQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarNyIdentitetQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarOppholdQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarOppholdsadresseQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarSikkerhetstiltakQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarTilrettelagtKommunikasjonQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarUtenlandskIdentifikasjonsnummerQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addHarUtflyttingQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addIdenttypeQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addKjoennQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addSivilstandQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addStatsborgerskapQuery;
import static no.nav.dolly.elastic.utils.OpenSearchPersonQueryUtils.addVergemaalQuery;

@UtilityClass
public class OpenSearchQueryBuilder {

    private static Random seed = new SecureRandom();

    public static BoolQueryBuilder buildSearchQuery(SearchRequest request) {

        var queryBuilder = QueryBuilders.boolQuery()
                .must(getRandomScoreQueryBuilder());

        request.getTyper().stream()
                .map(OpenSearchQueryBuilder::getFagsystemQuery)
                .forEach(queryBuilder::must);

       setPersonQuery(queryBuilder, request);

        return queryBuilder;
    }

    public static BoolQueryBuilder buildTyperQuery(ElasticTyper[] typer) {

        var queryBuilder = QueryBuilders.boolQuery()
                .must(getRandomScoreQueryBuilder());

        Arrays.stream(typer)
                .map(OpenSearchQueryBuilder::getFagsystemQuery)
                .forEach(queryBuilder::must);

        return queryBuilder;
    }

    private void setPersonQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest())
                .ifPresent(value -> {
                    addBarnQuery(queryBuilder, request);
                    addForeldreQuery(queryBuilder, request);
                    addSivilstandQuery(queryBuilder, request);
                    addHarDoedfoedtbarnQuery(queryBuilder, request);
                    addHarForeldreansvarQuery(queryBuilder, request);
                    addVergemaalQuery(queryBuilder, request);
                    addFullmaktQuery(queryBuilder, request);
                    addDoedsfallQuery(queryBuilder, request);
                    addHarInnflyttingQuery(queryBuilder, request);
                    addHarUtflyttingQuery(queryBuilder, request);
                    addAdressebeskyttelseQuery(queryBuilder, request);
                    addHarOppholdsadresseQuery(queryBuilder, request);
                    addHarKontaktadresseQuery(queryBuilder, request);
                    addBostedKommuneQuery(queryBuilder, request);
                    addBostedPostnrQuery(queryBuilder, request);
                    addBostedBydelsnrQuery(queryBuilder, request);
                    addHarBostedBydelsnrQuery(queryBuilder, request);
                    addBostedUtlandQuery(queryBuilder, request);
                    addBostedMatrikkelQuery(queryBuilder, request);
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
            case SIGRUN_LIGNET -> QueryBuilders.existsQuery("sigrunInntekt");
            case SIGRUN_PENSJONSGIVENDE -> QueryBuilders.existsQuery("sigrunPensjonsgivende");
            case ARENA_BRUKER -> QueryBuilders.existsQuery("arenaBruker");
            case ARENA_AAP -> QueryBuilders.existsQuery("arenaAap");
            case ARENA_AAP115 -> QueryBuilders.existsQuery("arenaAap115");
            case ARENA_DAGP -> QueryBuilders.existsQuery("arenaDagpenger");
            case UDISTUB -> QueryBuilders.existsQuery("udistub");
            case INNTK -> QueryBuilders.existsQuery("inntektstub");
            case PEN_INNTEKT -> QueryBuilders.existsQuery("penInntekt");
            case PEN_TP -> QueryBuilders.existsQuery("penTp");
            case PEN_AP -> QueryBuilders.existsQuery("penAlderspensjon");
            case PEN_UT -> QueryBuilders.existsQuery("penUforetrygd");
            case INNTKMELD -> QueryBuilders.existsQuery("inntektsmelding");
            case BRREGSTUB -> QueryBuilders.existsQuery("brregstub");
            case DOKARKIV -> QueryBuilders.existsQuery("dokarkiv");
            case MEDL -> QueryBuilders.existsQuery("medl");
            case HISTARK -> QueryBuilders.existsQuery("histark");
            case SYKEMELDING -> QueryBuilders.existsQuery("sykemelding");
            case SKJERMING -> QueryBuilders.existsQuery("skjerming");
            case BANKKONTO -> QueryBuilders.existsQuery("bankkonto");
            case BANKKONTO_NORGE -> QueryBuilders.existsQuery("bankkonto.norskBankkonto");
            case BANKKONTO_UTLAND -> QueryBuilders.existsQuery("bankkonto.utenlandskBankkonto");
            case ARBEIDSPLASSENCV -> QueryBuilders.existsQuery("arbeidsplassenCV");
        };
    }
    private static FunctionScoreQueryBuilder getRandomScoreQueryBuilder() {

        return QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder().seed(seed.nextInt()));
    }
}
