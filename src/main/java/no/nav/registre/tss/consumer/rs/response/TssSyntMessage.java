package no.nav.registre.tss.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class TssSyntMessage {

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
