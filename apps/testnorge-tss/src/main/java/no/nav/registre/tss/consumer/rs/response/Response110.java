package no.nav.registre.tss.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response110 {

    private String idKode;
    private String idOff;
    private String kodeIdenttype;
    private String kodeSamhType;
    private String beskrSamhType;
    private String datoSamhType;
    private String datoSamhTom;
    private String navn;
    private String kodeSpraak;
    private String etatsmerke;
    private String utbetalingssperre;
    private String kodeKontrint;
    private String beskrKontrint;
    private String kodeStatus;
    private String beskrStatus;
    private String oppdater;
    private String kilde;
    private String brukerId;
    private String tidReg;
}
