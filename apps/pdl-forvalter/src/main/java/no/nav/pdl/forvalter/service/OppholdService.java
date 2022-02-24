package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class OppholdService extends PdlArtifactService<OppholdDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: oppholdFra må være før oppholdTil";
    private static final String VALIDATION_TYPE_ERROR = "Type av opphold må angis";
    private static final String VALIDATION_OPPHOLD_OVELAP_ERROR = "Feil: Overlappende opphold er detektert";

    @Override
    public void validate(OppholdDTO opphold) {

        if (isNull(opphold.getType())) {
            throw new InvalidRequestException(VALIDATION_TYPE_ERROR);
        }

        if (nonNull(opphold.getOppholdFra()) && nonNull(opphold.getOppholdTil()) && !opphold.getOppholdFra().isBefore(opphold.getOppholdTil())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }
    }

    @Override
    protected void handle(OppholdDTO type) {

        // Ingen håndtering for enkeltpost
    }

    @Override
    protected void enforceIntegrity(List<OppholdDTO> opphold) {

        for (var i = 0; i < opphold.size(); i++) {
            if (i + 1 < opphold.size()) {
                if (isNull(opphold.get(i + 1).getOppholdTil()) &&
                        !opphold.get(i).getOppholdFra().isAfter(opphold.get(i + 1).getOppholdFra().plusDays(1)) ||
                        (nonNull(opphold.get(i + 1).getOppholdTil()) &&
                                !opphold.get(i).getOppholdFra().isAfter(opphold.get(i + 1).getOppholdTil()))) {
                    throw new InvalidRequestException(VALIDATION_OPPHOLD_OVELAP_ERROR);
                }
                if (isNull(opphold.get(i + 1).getOppholdTil())) {
                    opphold.get(i + 1).setOppholdTil(opphold.get(i).getOppholdFra().minusDays(1));
                }
            }
        }
    }
}