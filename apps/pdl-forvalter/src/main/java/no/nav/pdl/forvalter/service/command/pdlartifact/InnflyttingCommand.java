package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.dto.RsInnflytting;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InnflyttingCommand extends PdlArtifactService<RsInnflytting> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland";
    private static final String VALIDATION_INNFLYTTING_DATO_ERROR = "Ugyldig flyttedato, ny dato må være etter en eksisterende";

    public InnflyttingCommand(List<RsInnflytting> request) {
        super(request);
    }

    @Override
    protected void validate(RsInnflytting innflytting) {

        if (isNotBlank(innflytting.getFraflyttingsland()) && !isLandkode(innflytting.getFraflyttingsland())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ERROR);
        }
    }

    @Override
    public void handle(RsInnflytting innflytting) {

        if (isBlank(innflytting.getFraflyttingsland())) {
            innflytting.setFraflyttingsland(new TilfeldigLandCommand().call());
        }
    }

    @Override
    protected void enforceIntegrity(List<RsInnflytting> innflytting) {

        for (var i = 0; i < innflytting.size(); i++) {
            if (i + 1 < innflytting.size()) {
                if (nonNull(innflytting.get(i + 1).getFlyttedato()) &&
                        nonNull(innflytting.get(i).getFlyttedato()) &&
                        !innflytting.get(i).getFlyttedato()
                                .isAfter(innflytting.get(i + 1).getFlyttedato())) {
                    throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_INNFLYTTING_DATO_ERROR);
                }
            }
        }
    }
}
