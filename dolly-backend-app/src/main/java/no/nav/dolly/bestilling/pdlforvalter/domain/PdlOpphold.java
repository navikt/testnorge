package no.nav.dolly.bestilling.pdlforvalter.domain;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold.OppholdType.MIDLERTIDIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold.OppholdType.OPPLYSNING_MANGLER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold.OppholdType.PERMANENT;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdsrettType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOpphold {

    public enum OppholdType {MIDLERTIDIG, PERMANENT, OPPLYSNING_MANGLER}

    private String kilde;
    private LocalDate oppholdFra;
    private LocalDate oppholdTil;
    private OppholdType type;

    public static OppholdType getOppholdType(UdiOppholdsrettType udiOppholdType) {

        if (isNull(udiOppholdType)) {
            return OPPLYSNING_MANGLER;
        }
        switch (udiOppholdType) {
        case VARIG:
            return PERMANENT;
        case FAMILIE:
        case TJENESTEYTING_ELLER_ETABLERING:
            return MIDLERTIDIG;
        case UAVKLART:
        case INGEN_INFORMASJON:
        default:
            return OPPLYSNING_MANGLER;
        }
    }
}
