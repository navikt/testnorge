package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlOpphold;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class OppholdCommand extends PdlArtifactService<PdlOpphold> {

    private static final String VALIDATION_OPPHOLD_ERROR = "Ugyldig datointervall: oppholdFra må være før oppholdTil";

    public OppholdCommand(List<PdlOpphold> request) {
        super(request);
    }

    @Override
    protected void validate(PdlOpphold opphold) {

        if (opphold.getOppholdFra().isAfter(opphold.getOppholdTil())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_OPPHOLD_ERROR);
        }
    }

    @Override
    public void handle(PdlOpphold type) {

    }
}
