package no.nav.dolly.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Testident.Master;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BestillingProgressDTO implements Serializable {

    private LocalDateTime sistOppdatert;
    private LocalDate bestillingDato;

    private Long bestillingId;

    private String ident;
    private Master master;

    private String aaregStatus;
    private String arbeidsplassencvStatus;
    private String arbeidssoekerregisteretStatus;
    private String arenaforvalterStatus;
    private String bistandsbehovStatus;
    private String brregstubStatus;
    private String dokarkivStatus;
    private String etterlatteStatus;
    private String feil;
    private String fullmaktStatus;
    private String histarkStatus;
    private String inntektsmeldingStatus;
    private String inntektsstubStatus;
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
}
