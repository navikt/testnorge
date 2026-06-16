package no.nav.dolly.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Testident.Master;

import java.io.Serializable;
import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BestillingProgressDTO implements Serializable {

    private LocalDate sistOppdatert;

    private Long bestillingId;

    private String ident;
    private Master master;

    private String aaregStatus;
    private String arbeidsplassenCVStatus;
    private String arbeidssoekerregisteretStatus;
    private String arenaforvalterStatus;
    private String brregstubStatus;
    private String dokarkivStatus;
    private String etterlatteStatus;
    private String feil;
    private String fullmaktStatus;
    private String histarkStatus;
    private String inntektsmeldingStatus;
    private String inntektstubStatus;
    private String instdataStatus;
    private String kelvinAapStatus;
    private String kontoregisterStatus;
    private String krrstubStatus;
    private String medlStatus;
    private String nomStatus;
    private String pdlForvalterStatus;
    private String pdlImportStatus;
    private String pdlOrdreStatus;
    private String pdlPersonStatus;
    private String pensjonforvalterStatus;
    private String sigrunstubStatus;
    private String skattekortStatus;
    private String skjermingsregisterStatus;
    private String sykemeldingStatus;
    private String udistubStatus;
    private String yrkesskadeStatus;

    @Override
    public String toString() {
        return "{" +
               "\"sistOppdatert\":" + sistOppdatert +
               ", \"bestillingId\":" + bestillingId +
               ", \"ident\":\"" + ident + "\"" +
               ", \"master\":\"" +  master.toString() + "\"" +
               getError("aaregStatus" , aaregStatus) +
               getError("arbeidsplassenCVStatus" , arbeidsplassenCVStatus) +
               getError("arbeidssoekerregisteretStatus" , arbeidssoekerregisteretStatus) +
               getError("arenaforvalterStatus" , arenaforvalterStatus) +
               getError("brregstubStatus" , brregstubStatus) +
               getError("dokarkivStatus" , dokarkivStatus) +
               getError("etterlatteStatus" , etterlatteStatus) +
               getError("feil" , feil) +
               getError("fullmaktStatus" , fullmaktStatus) +
               getError("histarkStatus" , histarkStatus) +
               getError("inntektsmeldingStatus" , inntektsmeldingStatus) +
               getError("inntektstubStatus" , inntektstubStatus) +
               getError("instdataStatus" , instdataStatus) +
               getError("kelvinAapStatus" , kelvinAapStatus) +
               getError("kontoregisterStatus" , kontoregisterStatus) +
               getError("krrstubStatus" , krrstubStatus) +
               getError("medlStatus" , medlStatus) +
               getError("nomStatus" , nomStatus) +
               getError("pdlForvalterStatus" , pdlForvalterStatus) +
               getError("pdlImportStatus" , pdlImportStatus) +
               getError("pdlOrdreStatus" , isNotBlank(pdlOrdreStatus) ?
                       pdlOrdreStatus.substring(15) : EMPTY) +
               getError("pdlPersonStatus" , pdlPersonStatus) +
               getError("pensjonforvalterStatus" , pensjonforvalterStatus) +
               getError("sigrunstubStatus" , sigrunstubStatus) +
               getError("skattekortStatus" , skattekortStatus) +
               getError("skjermingsregisterStatus" , skjermingsregisterStatus) +
               getError("sykemeldingStatus" , sykemeldingStatus) +
               getError("udistubStatus" , udistubStatus) +
               getError("yrkesskadeStatus" , yrkesskadeStatus) +
               "}";
    }

    private static String getError(String kolonnenavn, String kolonneverdi) {

        return isNotBlank(kolonneverdi) && kolonneverdi.toLowerCase().contains("feil") ?
                ", \"%s\":\"%s\"".formatted(kolonnenavn, kolonneverdi) : "";
    }
}
