package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlOpphold;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class OppholdCommand extends PdlArtifactService<PdlOpphold> {

    private static final String VALIDATION_OPPHOLD_FRA_ERROR = "Opphold med oppholdFra må angis";
    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: oppholdFra må være før oppholdTil";
    private static final String VALIDATION_TYPE_ERROR = "Type av opphold må angis";
    private static final String VALIDATION_OPPHOLD_OVELAP_ERROR = "Feil: Overlappende opphold er detektert";

    public OppholdCommand(List<PdlOpphold> request) {
        super(request);
    }

    @Override
    protected void validate(PdlOpphold opphold) {

        if (isNull(opphold.getType())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_TYPE_ERROR);
        }

        if (isNull(opphold.getOppholdFra())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_OPPHOLD_FRA_ERROR);

        } else if (nonNull(opphold.getOppholdTil())) {

            if (!opphold.getOppholdFra().isBefore(opphold.getOppholdTil())) {
                throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UGYLDIG_INTERVAL_ERROR);
            }
        }
    }

    @Override
    protected void handle(PdlOpphold type) {

    }

    @Override
    protected void enforceIntegrity(List<PdlOpphold> opphold) {

        for (var i = 0; i < opphold.size(); i++) {
            if (i + 1 < opphold.size()) {
                if (isNull(opphold.get(i + 1).getOppholdTil()) &&
                        !opphold.get(i).getOppholdFra().isAfter(opphold.get(i + 1).getOppholdFra().plusDays(1)) ||
                        (nonNull(opphold.get(i + 1).getOppholdTil()) &&
                                !opphold.get(i).getOppholdFra().isAfter(opphold.get(i + 1).getOppholdTil()))) {
                    throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_OPPHOLD_OVELAP_ERROR);
                }
                if (isNull(opphold.get(i + 1).getOppholdTil())) {
                    opphold.get(i + 1).setOppholdTil(opphold.get(i).getOppholdFra().minusDays(1));
                }
            }
        }
    }
}