package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Master;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlAdressebeskyttelse {

    public enum AdresseBeskyttelse {STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT}

    private AdresseBeskyttelse gradering;
    private String kilde;
    private Master master;
}
