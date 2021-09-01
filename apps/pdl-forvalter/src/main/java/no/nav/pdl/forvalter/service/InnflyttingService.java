package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class InnflyttingService extends PdlArtifactService<InnflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland";

    private final TilfeldigLandService tilfeldigLandService;

    @Override
    protected void validate(InnflyttingDTO innflytting) {

        if (isNotBlank(innflytting.getFraflyttingsland()) && !isLandkode(innflytting.getFraflyttingsland())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }
    }

    @Override
    protected void handle(InnflyttingDTO innflytting) {

        if (isBlank(innflytting.getFraflyttingsland())) {
            innflytting.setFraflyttingsland(tilfeldigLandService.getLand());
        }
    }

    @Override
    protected void enforceIntegrity(List<InnflyttingDTO> adresse) {

        // No integrity check
    }
}
