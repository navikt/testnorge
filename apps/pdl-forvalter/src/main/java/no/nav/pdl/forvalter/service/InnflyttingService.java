package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class InnflyttingService extends PdlArtifactService<InnflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland";
    private static final String VALIDATION_INNFLYTTING_OVELAP_ERROR = "Innflytting: Ugyldig flyttedato, ny dato må være etter en eksisterende";

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

        for (var i = 0; i < adresse.size(); i++) {
            if (i + 1 < adresse.size()) {
                if (isOverlapGyldigTom(adresse, i) || isOverlapGyldigFom(adresse, i)) {
                    throw new InvalidRequestException(VALIDATION_INNFLYTTING_OVELAP_ERROR);
                }
            }
        }
    }

    private boolean isOverlapGyldigFom(List<InnflyttingDTO> adresse, int i) {
        return nonNull(adresse.get(i + 1).getGyldigTilOgMed()) && nonNull(adresse.get(i).getGyldigFraOgMed()) &&
                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigTilOgMed());
    }

    private boolean isOverlapGyldigTom(List<InnflyttingDTO> adresse, int i) {
        return isNull(adresse.get(i + 1).getGyldigTilOgMed()) &&
                nonNull(adresse.get(i).getGyldigFraOgMed()) && nonNull(adresse.get(i + 1).getGyldigFraOgMed()) &&
                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigFraOgMed().plusDays(1));
    }
}
