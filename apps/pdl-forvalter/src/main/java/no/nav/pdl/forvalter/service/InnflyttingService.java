package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class InnflyttingService extends PdlArtifactService<InnflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland";
    private static final String VALIDATION_INNFLYTTING_DATO_ERROR = "Ugyldig flyttedato, ny dato må være etter en eksisterende";

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
    protected void enforceIntegrity(List<InnflyttingDTO> innflytting) {

        for (var i = 0; i < innflytting.size(); i++) {
            if (i + 1 < innflytting.size() && nonNull(innflytting.get(i + 1).getFlyttedato()) &&
                    nonNull(innflytting.get(i).getFlyttedato()) &&
                    !innflytting.get(i).getFlyttedato()
                            .isAfter(innflytting.get(i + 1).getFlyttedato())) {
                throw new InvalidRequestException(VALIDATION_INNFLYTTING_DATO_ERROR);
            }
        }
    }
}
