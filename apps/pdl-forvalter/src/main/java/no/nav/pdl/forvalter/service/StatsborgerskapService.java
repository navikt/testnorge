package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.InnflyttingDTO;
import no.nav.pdl.forvalter.domain.PersonDTO;
import no.nav.pdl.forvalter.domain.StatsborgerskapDTO;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class StatsborgerskapService {

    private static final String VALIDATION_LANDKODE_ERROR = "Ugyldig landkode, må være i hht ISO-3 Landkoder";
    private static final String VALIDATION_DATOINTERVALL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";

    private final TilfeldigLandService tilfeldigLandService;

    public List<StatsborgerskapDTO> convert(PersonDTO person) {

        for (var type : person.getStatsborgerskap()) {

            if (type.isNew()) {
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
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ERROR);
        }

        if (nonNull(statsborgerskap.getGyldigFom()) && nonNull(statsborgerskap.getGyldigTom()) &&
                !statsborgerskap.getGyldigFom().isBefore(statsborgerskap.getGyldigTom())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_DATOINTERVALL_ERROR);
        }
    }

    private void handle(StatsborgerskapDTO statsborgerskap, String ident, InnflyttingDTO innflytting) {

        if (isBlank(statsborgerskap.getLandkode())) {
            if (nonNull(innflytting)) {
                statsborgerskap.setLandkode(innflytting.getFraflyttingsland());
            } else if (Identtype.FNR.equals(IdenttypeFraIdentUtility.getIdenttype(ident))) {
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
