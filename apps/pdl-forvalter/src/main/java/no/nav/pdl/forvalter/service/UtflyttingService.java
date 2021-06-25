package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UtflyttingService extends PdlArtifactService<UtflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland";
    private static final String VALIDATION_UTFLYTTING_DATO_ERROR = "Ugyldig flyttedato, ny dato må være etter en eksisterende";

    private final TilfeldigLandService tilfeldigLandService;

    @Override
    protected void validate(UtflyttingDTO utflytting) {

        if (isNotBlank(utflytting.getTilflyttingsland()) && !isLandkode(utflytting.getTilflyttingsland())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }
    }

    @Override
    protected void handle(UtflyttingDTO utflytting) {

        if (isBlank(utflytting.getTilflyttingsland())) {
            utflytting.setTilflyttingsland(tilfeldigLandService.getLand());
        }
    }

    @Override
    protected void enforceIntegrity(List<UtflyttingDTO> utflytting) {

        for (var i = 0; i < utflytting.size(); i++) {
            if (i + 1 < utflytting.size() && nonNull(utflytting.get(i + 1).getFlyttedato()) &&
                    nonNull(utflytting.get(i).getFlyttedato()) &&
                    !utflytting.get(i).getFlyttedato()
                            .isAfter(utflytting.get(i + 1).getFlyttedato())) {
                throw new InvalidRequestException(VALIDATION_UTFLYTTING_DATO_ERROR);
            }
        }
    }
}
