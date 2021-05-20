package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlFamilierelasjon {

    public enum ROLLE {MOR, FAR, MEDMOR, BARN}

    private String kilde;
    private ROLLE minRolleForPerson;
    private String relatertPerson;
    private ROLLE relatertPersonsRolle;

    public static ROLLE decode(Relasjon.ROLLE rolle) {

        if (isNull(rolle)) {
            return null;
        }

        switch (rolle) {
        case MOR:
            return ROLLE.MOR;
        case FAR:
            return ROLLE.FAR;
        default:
        case FOEDSEL:
        case BARN:
            return ROLLE.BARN;
        }
    }
}
