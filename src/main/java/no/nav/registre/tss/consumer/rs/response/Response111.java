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
public class Response111 {

    private String idKode;
    private String idAlternativ;
    private String kodeAltIdenttype;
    private String beskrAltIdenttype;
    private String datoIdentFom;
    private String datoIdentTom;
    private String gyldigIdent;
    private String kilde;
    private String brukerid;
    private String tidReg;
}
