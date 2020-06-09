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
public class Response125 {

    private String idKode;
    private String avdelingsnr;
    private String avdelingsnavn;
    private String typeAvdeling;
    private String beskrAvdelingstype;
    private String datoAvdelingFrom;
    private String datoAvdelingTom;
    private String gyldigAvdeling;
    private String idTSSEkstern;
    private String avdOffnr;
    private String kilde;
    private String brukerid;
    private String tidReg;
}
