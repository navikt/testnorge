package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlUtflytting;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UtflyttingCommand extends PdlArtifactService<PdlUtflytting> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i tilflyttingsland";

    public UtflyttingCommand(List<PdlUtflytting> request) {
        super(request);
    }

    @Override
    protected void validate(PdlUtflytting innflytting) {

        if (!isLandkode(innflytting.getTilflyttingsland())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ERROR);
        }
    }

    @Override
    public void handle(PdlUtflytting utflytting) {

        if (isBlank(utflytting.getTilflyttingsland())) {
            utflytting.setTilflyttingsland(new TilfeldigLandCommand().call());
        }
    }
}
