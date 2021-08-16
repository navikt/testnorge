package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UtflyttingService extends PdlArtifactService<UtflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland";
    private static final String VALIDATION_UTFYTTING_OVELAP_ERROR = "Utflytting: Ugyldig flyttedato, ny dato må være etter en eksisterende";

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
    protected void enforceIntegrity(List<UtflyttingDTO> adresse) {

        for (var i = 0; i < adresse.size(); i++) {
            if (i + 1 < adresse.size()) {
                if (isOverlapGyldigTom(adresse, i) || isOverlapGyldigFom(adresse, i)) {
                    throw new InvalidRequestException(VALIDATION_UTFYTTING_OVELAP_ERROR);
                }
            }
        }
    }

    private boolean isOverlapGyldigFom(List<UtflyttingDTO> adresse, int i) {
        return nonNull(adresse.get(i + 1).getGyldigTilOgMed()) && nonNull(adresse.get(i).getGyldigFraOgMed()) &&
                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigTilOgMed());
    }

    private boolean isOverlapGyldigTom(List<UtflyttingDTO> adresse, int i) {
        return isNull(adresse.get(i + 1).getGyldigTilOgMed()) &&
                nonNull(adresse.get(i).getGyldigFraOgMed()) && nonNull(adresse.get(i + 1).getGyldigFraOgMed()) &&
                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigFraOgMed().plusDays(1));
    }
}
