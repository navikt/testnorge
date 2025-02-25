package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.dollysearchservice.v2.ElasticTyper;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

@UtilityClass
public class FagsystemQuereyUtils {

    public static QueryBuilder getFagsystemQuery(ElasticTyper type) {

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
            case ARBEIDSSOEKERREGISTERET -> QueryBuilders.existsQuery("arbeidssoekerregisteret");
        };
    }
}
