package no.nav.registre.tss.consumer.rs.responses;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(NON_NULL)
public class TssMessage {

    private String idKode;
    private String idOff;
    private String kodeIdenttype;
    private String kodeSamhType;
    private String navn;
    private String oppdater;
    private String idAlternativ;
    private String kodeAltIdenttype;
    private String kodeAutortype;
    private String datoAutorisasjonFom;
    private String datoAutorisasjonTom;
    private String kodeAutRett;
    private String datoAutRettFom;
    private String datoAutRettTom;
}
