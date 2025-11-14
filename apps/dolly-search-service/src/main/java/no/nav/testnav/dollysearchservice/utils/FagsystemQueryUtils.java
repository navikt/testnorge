package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBase;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class FagsystemQueryUtils {

    private static final String ARBEIDSFORHOLDTYPE = "aareg.arbeidsforholdstype";

    public static ExistsQuery.Builder getFagsystemQuery(ElasticTyper type) {

        return switch (type) {
            case ARBEIDSFORHOLD -> QueryBuilders.exists().field("aareg");
            case ARBEIDSFORHOLD_FRILANS -> QueryBuilders.match().field(ARBEIDSFORHOLDTYPE)
                    .query(FieldValue.of("frilanserOppdragstakerHonorarPersonerMm"));
            case ARBEIDSFORHOLD_ORDINAERT -> QueryBuilders.match().field(ARBEIDSFORHOLDTYPE)
                    .query(FieldValue.of("ordinaertArbeidsforhold"));
            case ARBEIDSFORHOLD_MARITIMT -> QueryBuilders.match().field(ARBEIDSFORHOLDTYPE)
                    .query(FieldValue.of("maritimtArbeidsforhold"));
            case ARBEIDSFORHOLD_FORENKLET -> QueryBuilders.match().field(ARBEIDSFORHOLDTYPE)
                            .query(FieldValue.of("forenkletOppgjoersordning"));
            case ARBEIDSPLASSENCV -> QueryBuilders.exists().field("arbeidsplassenCV");
            case ARBEIDSSOEKERREGISTERET -> QueryBuilders.exists().field("arbeidssoekerregisteret");
            case ARENA_AAP -> QueryBuilders.exists().field("arenaforvalter.aap");
            case ARENA_AAP115 -> QueryBuilders.exists().field("arenaforvalter.aap115");
            case ARENA_DAGP -> QueryBuilders.exists().field("arenaforvalter.dagpenger");
            case BANKKONTO -> QueryBuilders.exists().field("bankkonto");
            case BANKKONTO_NORGE -> QueryBuilders.exists().field("bankkonto.norskBankkonto");
            case BANKKONTO_UTLAND -> QueryBuilders.exists().field("bankkonto.utenlandskBankkonto");
            case BRREGSTUB -> QueryBuilders.exists().field("brregstub");
            case DOKARKIV -> QueryBuilders.exists().field("dokarkiv");
            case ETTERLATTE -> QueryBuilders.exists().field("etterlatteYtelser");
            case FULLMAKT -> QueryBuilders.exists().field("fullmakt");
            case HISTARK -> QueryBuilders.exists().field("histark");
            case INNTK -> QueryBuilders.exists().field("inntektstub");
            case INNTKMELD -> QueryBuilders.exists().field("inntektsmelding");
            case INST -> QueryBuilders.exists().field("instdata");
            case KRRSTUB -> QueryBuilders.exists().field("krrstub");
            case MEDL -> QueryBuilders.exists().field("medl");
            case NOM -> QueryBuilders.exists().field("nomdata");
            case PEN_AFP_OFFENTLIG -> QueryBuilders.exists().field("pensjonforvalter.afpOffentlig");
            case PEN_AP -> QueryBuilders.exists().field("pensjonforvalter.alderspensjon");
            case PEN_AP_NY_UTTAKSGRAD -> QueryBuilders.exists().field("pensjonforvalter.alderspensjonNyUtaksgrad");
            case PEN_INNTEKT -> QueryBuilders.exists().field("pensjonforvalter.inntekt");
            case PEN_PENSJONSAVTALE -> QueryBuilders.exists().field("pensjonforvalter.pensjonsavtale");
            case PEN_TP -> QueryBuilders.exists().field("pensjonforvalter.tp");
            case PEN_UT -> QueryBuilders.exists().field("pensjonforvalter.uforetrygd");
            case SIGRUN_PENSJONSGIVENDE -> QueryBuilders.exists().field("sigrunstubPensjonsgivende");
            case SIGRUN_SUMMERT -> QueryBuilders.exists().field("sigrunstubSummertSkattegrunnlag");
            case SKATTEKORT -> QueryBuilders.exists().field("skattekort");
            case SKJERMING -> QueryBuilders.exists().field("skjerming");
            case SYKEMELDING -> QueryBuilders.exists().field("sykemelding");
            case UDISTUB -> QueryBuilders.exists().field("udistub");
            case YRKESSKADE -> QueryBuilders.exists().field("yrkesskader");
        };
    }

    public static void addMiljoerQuery(BoolQuery.Builder queryBuilder, List<String> miljoer) {

        if (!miljoer.isEmpty()) {
            miljoer.forEach(miljoe -> queryBuilder.must(builder ->  builder.match(QueryBuilders.match()
                    .field("miljoer")
                    .query(FieldValue.of(miljoe))
                    .build())));
        }
    }

    public static void addIdentQuery(BoolQuery.Builder queryBuilder, PersonRequest personRequest) {

        if (nonNull(personRequest) && isNotBlank(personRequest.getIdent())) {

            queryBuilder.must(builder -> builder.match(QueryBuilders.match()
                    .field("identer")
                    .query(FieldValue.of(personRequest.getIdent()))
                            .build()));
        }
    }
}