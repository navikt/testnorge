package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlForelderBarnRelasjon extends PdlOpplysning {

    private ROLLE minRolleForPerson;
    private String relatertPerson;
    private ROLLE relatertPersonsRolle;

    public static ROLLE decode(Relasjon.ROLLE rolle) {

        if (isNull(rolle)) {
            return null;
        }

        return switch (rolle) {
            case MOR -> ROLLE.MOR;
            case FAR -> ROLLE.FAR;
            default -> ROLLE.BARN;
        };
    }

    public enum ROLLE {MOR, FAR, MEDMOR, BARN}
}
