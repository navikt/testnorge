package no.nav.dolly.bestilling.pdlforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlAdressebeskyttelse {

    public enum AdresseBeskyttelse {STRENGT_FORTROLIG, FORTROLIG, UGRADERT}

    private AdresseBeskyttelse gradering;
    private String kilde;

    public static AdresseBeskyttelse convertSpesreg(String spesregkode) {

        if ("SPSF".equals(spesregkode)) {
            return AdresseBeskyttelse.STRENGT_FORTROLIG;

        } else if ("SPFO".equals(spesregkode)) {
            return AdresseBeskyttelse.FORTROLIG;

        } else {
            return AdresseBeskyttelse.UGRADERT;
        }
    }
}
