package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlAdresse.Master;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlAdressebeskyttelse extends PdlDbVersjon {

    public enum AdresseBeskyttelse {STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT}

    private AdresseBeskyttelse gradering;
    private String kilde;
    private Master master;
}
