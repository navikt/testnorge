package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlInnflytting;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InnflyttingCommand extends PdlArtifactService<PdlInnflytting> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i fraflyttingsland";

    public InnflyttingCommand(List<PdlInnflytting> request) {
        super(request);
    }

    @Override
    protected void validate(PdlInnflytting innflytting) {

        if (isNotBlank(innflytting.getFraflyttingsland()) && !isLandkode(innflytting.getFraflyttingsland())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ERROR);
        }
    }

    @Override
    public void handle(PdlInnflytting innflytting) {

        if (isBlank(innflytting.getFraflyttingsland())) {
            innflytting.setFraflyttingsland(new TilfeldigLandCommand().call());
        }
    }
}
