package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.matchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.mustExistQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.mustMatchQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class FagsystemQueryUtils {

    private static final String ARBEIDSFORHOLDTYPE = "aareg.arbeidsforholdstype";

    public static void addFagsystemQuery(BoolQuery.Builder queryBuilder, ElasticTyper type) {

        switch (type) {
            case ARBEIDSFORHOLD -> mustExistQuery(queryBuilder, "aareg");
            case ARBEIDSFORHOLD_FRILANS ->
                    mustMatchQuery(queryBuilder, ARBEIDSFORHOLDTYPE, "frilanserOppdragstakerHonorarPersonerMm");
            case ARBEIDSFORHOLD_ORDINAERT ->
                    mustMatchQuery(queryBuilder, ARBEIDSFORHOLDTYPE, "ordinaertArbeidsforhold");
            case ARBEIDSFORHOLD_MARITIMT -> mustMatchQuery(queryBuilder, ARBEIDSFORHOLDTYPE, "maritimtArbeidsforhold");
            case ARBEIDSFORHOLD_FORENKLET ->
                    mustMatchQuery(queryBuilder, ARBEIDSFORHOLDTYPE, "forenkletOppgjoersordning");
            case ARBEIDSPLASSENCV -> mustExistQuery(queryBuilder, "arbeidsplassenCV");
            case ARBEIDSSOEKERREGISTERET -> mustExistQuery(queryBuilder, "arbeidssoekerregisteret");
            case ARENA_AAP -> mustExistQuery(queryBuilder, "arenaforvalter.aap");
            case ARENA_AAP115 -> mustExistQuery(queryBuilder, "arenaforvalter.aap115");
            case ARENA_DAGP -> mustExistQuery(queryBuilder, "arenaforvalter.dagpenger");
            case BANKKONTO -> mustExistQuery(queryBuilder, "bankkonto");
            case BANKKONTO_NORGE -> mustExistQuery(queryBuilder, "bankkonto.norskBankkonto");
            case BANKKONTO_UTLAND -> mustExistQuery(queryBuilder, "bankkonto.utenlandskBankkonto");
            case BRREGSTUB -> mustExistQuery(queryBuilder, "brregstub");
            case DOKARKIV -> mustExistQuery(queryBuilder, "dokarkiv");
            case ETTERLATTE -> mustExistQuery(queryBuilder, "etterlatteYtelser");
            case FULLMAKT -> mustExistQuery(queryBuilder, "fullmakt");
            case HISTARK -> mustExistQuery(queryBuilder, "histark");
            case INNTK -> mustExistQuery(queryBuilder, "inntektstub");
            case INNTKMELD -> mustExistQuery(queryBuilder, "inntektsmelding");
            case INST -> mustExistQuery(queryBuilder, "instdata");
            case KRRSTUB -> mustExistQuery(queryBuilder, "krrstub");
            case MEDL -> mustExistQuery(queryBuilder, "medl");
            case NOM -> mustExistQuery(queryBuilder, "nomdata");
            case PEN_AFP_OFFENTLIG -> mustExistQuery(queryBuilder, "pensjonforvalter.afpOffentlig");
            case PEN_AP -> mustExistQuery(queryBuilder, "pensjonforvalter.alderspensjon");
            case PEN_AP_NY_UTTAKSGRAD -> mustExistQuery(queryBuilder, "pensjonforvalter.alderspensjonNyUtaksgrad");
            case PEN_INNTEKT -> mustExistQuery(queryBuilder, "pensjonforvalter.inntekt");
            case PEN_PENSJONSAVTALE -> mustExistQuery(queryBuilder, "pensjonforvalter.pensjonsavtale");
            case PEN_TP -> mustExistQuery(queryBuilder, "pensjonforvalter.tp");
            case PEN_UT -> mustExistQuery(queryBuilder, "pensjonforvalter.uforetrygd");
            case SIGRUN_PENSJONSGIVENDE -> mustExistQuery(queryBuilder, "sigrunstubPensjonsgivende");
            case SIGRUN_SUMMERT -> mustExistQuery(queryBuilder, "sigrunstubSummertSkattegrunnlag");
            case SKATTEKORT -> mustExistQuery(queryBuilder, "skattekort");
            case SKJERMING -> mustExistQuery(queryBuilder, "skjerming");
            case SYKEMELDING -> mustExistQuery(queryBuilder, "sykemelding");
            case UDISTUB -> mustExistQuery(queryBuilder, "udistub");
            case YRKESSKADE -> mustExistQuery(queryBuilder, "yrkesskader");
        }
    }

    public static void addMiljoerQuery(BoolQuery.Builder queryBuilder, List<String> miljoer) {

        if (!miljoer.isEmpty()) {
            miljoer.forEach(miljoe -> queryBuilder.must(builder ->
                    builder.match(matchQuery("miljoer", miljoe))));
        }
    }

    public static void addIdentQuery(BoolQuery.Builder queryBuilder, PersonRequest personRequest) {

        if (nonNull(personRequest) && isNotBlank(personRequest.getIdent())) {
            queryBuilder.must(builder ->
                    builder.match(matchQuery("identer", personRequest.getIdent())));
        }
    }
}