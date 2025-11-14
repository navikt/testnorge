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
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.existQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class FagsystemQueryUtils {

    private static final String ARBEIDSFORHOLDTYPE = "aareg.arbeidsforholdstype";

    public static ExistsQuery getFagsystemQuery(ElasticTyper type) {

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
            case ARBEIDSPLASSENCV -> existQuery("arbeidsplassenCV");
            case ARBEIDSSOEKERREGISTERET -> existQuery("arbeidssoekerregisteret");
            case ARENA_AAP -> existQuery("arenaforvalter.aap");
            case ARENA_AAP115 -> existQuery("arenaforvalter.aap115");
            case ARENA_DAGP -> existQuery("arenaforvalter.dagpenger");
            case BANKKONTO -> existQuery("bankkonto");
            case BANKKONTO_NORGE -> existQuery("bankkonto.norskBankkonto");
            case BANKKONTO_UTLAND -> existQuery("bankkonto.utenlandskBankkonto");
            case BRREGSTUB -> existQuery("brregstub");
            case DOKARKIV -> existQuery("dokarkiv");
            case ETTERLATTE -> existQuery("etterlatteYtelser");
            case FULLMAKT -> existQuery("fullmakt");
            case HISTARK -> existQuery("histark");
            case INNTK -> existQuery("inntektstub");
            case INNTKMELD -> existQuery("inntektsmelding");
            case INST -> existQuery("instdata");
            case KRRSTUB -> existQuery("krrstub");
            case MEDL -> existQuery("medl");
            case NOM -> existQuery("nomdata");
            case PEN_AFP_OFFENTLIG -> existQuery("pensjonforvalter.afpOffentlig");
            case PEN_AP -> existQuery("pensjonforvalter.alderspensjon");
            case PEN_AP_NY_UTTAKSGRAD -> existQuery("pensjonforvalter.alderspensjonNyUtaksgrad");
            case PEN_INNTEKT -> existQuery("pensjonforvalter.inntekt");
            case PEN_PENSJONSAVTALE -> existQuery("pensjonforvalter.pensjonsavtale");
            case PEN_TP -> existQuery("pensjonforvalter.tp");
            case PEN_UT -> existQuery("pensjonforvalter.uforetrygd");
            case SIGRUN_PENSJONSGIVENDE -> existQuery("sigrunstubPensjonsgivende");
            case SIGRUN_SUMMERT -> existQuery("sigrunstubSummertSkattegrunnlag");
            case SKATTEKORT -> existQuery("skattekort");
            case SKJERMING -> existQuery("skjerming");
            case SYKEMELDING -> existQuery("sykemelding");
            case UDISTUB -> existQuery("udistub");
            case YRKESSKADE -> existQuery("yrkesskader");
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