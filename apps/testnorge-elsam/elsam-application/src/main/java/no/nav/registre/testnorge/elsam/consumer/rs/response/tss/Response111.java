package no.nav.registre.testnorge.elsam.consumer.rs.response.tss;

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
    private IdentKode kodeAltIdenttype;
    private String beskrAltIdenttype;
    private String datoIdentFom;
    private String datoIdentTom;
    private String gyldigIdent;
    private String kilde;
    private String brukerid;
    private String tidReg;
}
