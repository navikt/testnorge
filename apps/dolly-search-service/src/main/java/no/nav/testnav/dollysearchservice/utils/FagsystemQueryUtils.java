package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.List;

import static java.util.Objects.nonNull;

@UtilityClass
public class FagsystemQueryUtils {

    private static final String ARBEIDSFORHOLDTYPE = "aareg.arbeidsforholdstype";

    public static QueryBuilder getFagsystemQuery(ElasticTyper type) {

        return switch (type) {
            case ARBEIDSFORHOLD -> QueryBuilders.existsQuery("aareg");
            case ARBEIDSFORHOLD_FRILANS ->
                    QueryBuilders.matchQuery(ARBEIDSFORHOLDTYPE, "frilanserOppdragstakerHonorarPersonerMm");
            case ARBEIDSFORHOLD_ORDINAERT ->
                    QueryBuilders.matchQuery(ARBEIDSFORHOLDTYPE, "ordinaertArbeidsforhold");
            case ARBEIDSFORHOLD_MARITIMT ->
                    QueryBuilders.matchQuery(ARBEIDSFORHOLDTYPE, "maritimtArbeidsforhold");
            case ARBEIDSFORHOLD_FORENKLET ->
                    QueryBuilders.matchQuery(ARBEIDSFORHOLDTYPE, "forenkletOppgjoersordning");
            case ARBEIDSPLASSENCV -> QueryBuilders.existsQuery("arbeidsplassenCV");
            case ARBEIDSSOEKERREGISTERET -> QueryBuilders.existsQuery("arbeidssoekerregisteret");
            case ARENA_AAP -> QueryBuilders.existsQuery("arenaforvalter.aap");
            case ARENA_AAP115 -> QueryBuilders.existsQuery("arenaforvalter.aap115");
            case ARENA_DAGP -> QueryBuilders.existsQuery("arenaforvalter.dagpenger");
            case BANKKONTO -> QueryBuilders.existsQuery("bankkonto");
            case BANKKONTO_NORGE -> QueryBuilders.existsQuery("bankkonto.norskBankkonto");
            case BANKKONTO_UTLAND -> QueryBuilders.existsQuery("bankkonto.utenlandskBankkonto");
            case BRREGSTUB -> QueryBuilders.existsQuery("brregstub");
            case DOKARKIV -> QueryBuilders.existsQuery("dokarkiv");
            case ETTERLATTE ->  QueryBuilders.existsQuery("etterlatteYtelser");
            case FULLMAKT -> QueryBuilders.existsQuery("fullmakt");
            case HISTARK -> QueryBuilders.existsQuery("histark");
            case INNTK -> QueryBuilders.existsQuery("inntektstub");
            case INNTKMELD -> QueryBuilders.existsQuery("inntektsmelding");
            case INST -> QueryBuilders.existsQuery("instdata");
            case KRRSTUB -> QueryBuilders.existsQuery("krrstub");
            case MEDL -> QueryBuilders.existsQuery("medl");
            case NOM -> QueryBuilders.existsQuery("nomdata");
            case PEN_AFP_OFFENTLIG -> QueryBuilders.existsQuery("pensjonforvalter.afpOffentlig");
            case PEN_AP -> QueryBuilders.existsQuery("pensjonforvalter.alderspensjon");
            case PEN_INNTEKT -> QueryBuilders.existsQuery("pensjonforvalter.inntekt");
            case PEN_PENSJONSAVTALE -> QueryBuilders.existsQuery("pensjonforvalter.pensjonsavtale");
            case PEN_TP -> QueryBuilders.existsQuery("pensjonforvalter.tp");
            case PEN_UT -> QueryBuilders.existsQuery("pensjonforvalter.uforetrygd");
            case SIGRUN_PENSJONSGIVENDE -> QueryBuilders.existsQuery("sigrunstubPensjonsgivende");
            case SIGRUN_SUMMERT -> QueryBuilders.existsQuery("sigrunstubSummertSkattegrunnlag");
            case SKATTEKORT -> QueryBuilders.existsQuery("skattekort");
            case SKJERMING -> QueryBuilders.existsQuery("skjerming");
            case SYKEMELDING -> QueryBuilders.existsQuery("sykemelding");
            case UDISTUB -> QueryBuilders.existsQuery("udistub");
            case YRKESSKADE -> QueryBuilders.existsQuery("yrkesskader");
        };
    }

    public static void addMiljoerQuery(BoolQueryBuilder queryBuilder, List<String> miljoer) {

        if (!miljoer.isEmpty()) {
            var boolQuery = QueryBuilders.boolQuery();
            miljoer.forEach(miljoe -> boolQuery.must(QueryBuilders.matchQuery("miljoer", miljoe)));
            queryBuilder.must(boolQuery);
        }
    }

    public static void addIdentQuery(BoolQueryBuilder queryBuilder, PersonRequest personRequest) {

        if (nonNull(personRequest) && StringUtils.isNotBlank(personRequest.getIdent())) {
            var boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.matchQuery("identer", personRequest.getIdent()));
            queryBuilder.must(boolQuery);
        }
    }
}