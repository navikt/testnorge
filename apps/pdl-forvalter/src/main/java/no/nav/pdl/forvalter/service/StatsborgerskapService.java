package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class StatsborgerskapService {

    private static final String VALIDATION_LANDKODE_ERROR = "Ugyldig landkode, må være i hht ISO-3 Landkoder";
    private static final String VALIDATION_DATOINTERVALL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";

    private final TilfeldigLandService tilfeldigLandService;

    public List<StatsborgerskapDTO> convert(PersonDTO person) {

        for (var type : person.getStatsborgerskap()) {

            if (isTrue(type.getIsNew())) {
                validate(type);

                handle(type, person.getIdent(), person.getInnflytting().stream().reduce((a, b) -> b).orElse(null));
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getStatsborgerskap();
    }

    private void validate(StatsborgerskapDTO statsborgerskap) {

        if (nonNull(statsborgerskap.getLandkode()) && !isLandkode(statsborgerskap.getLandkode())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }

        if (nonNull(statsborgerskap.getGyldigFom()) && nonNull(statsborgerskap.getGyldigTom()) &&
                !statsborgerskap.getGyldigFom().isBefore(statsborgerskap.getGyldigTom())) {
            throw new InvalidRequestException(VALIDATION_DATOINTERVALL_ERROR);
        }
    }

    private void handle(StatsborgerskapDTO statsborgerskap, String ident, InnflyttingDTO innflytting) {

        if (isBlank(statsborgerskap.getLandkode())) {
            if (nonNull(innflytting)) {
                statsborgerskap.setLandkode(innflytting.getFraflyttingsland());
            } else if (FNR.equals(IdenttypeFraIdentUtility.getIdenttype(ident))) {
                statsborgerskap.setLandkode(NORGE);
            } else {
                statsborgerskap.setLandkode(tilfeldigLandService.getLand());
            }
        }

        if (isNull(statsborgerskap.getGyldigFom())) {
            statsborgerskap.setGyldigFom(DatoFraIdentUtility.getDato(ident).atStartOfDay());
        }
    }
}
