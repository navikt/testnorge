package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.hasLandkode;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class StatsborgerskapService implements Validation<StatsborgerskapDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Ugyldig landkode, må være i hht ISO-3 Landkoder";
    private static final String VALIDATION_DATOINTERVALL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";

    private final KodeverkConsumer kodeverkConsumer;

    public List<StatsborgerskapDTO> convert(PersonDTO person) {

        for (var type : person.getStatsborgerskap()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
                handle(type, person, person.getInnflytting().stream().reduce((a, b) -> b).orElse(null));
            }
        }
        return person.getStatsborgerskap();
    }

    @Override
    public void validate(StatsborgerskapDTO statsborgerskap) {

        if (nonNull(statsborgerskap.getLandkode()) && !hasLandkode(statsborgerskap.getLandkode())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }

        if (nonNull(statsborgerskap.getGyldigFraOgMed()) && nonNull(statsborgerskap.getGyldigTilOgMed()) &&
                !statsborgerskap.getGyldigFraOgMed().isBefore(statsborgerskap.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_DATOINTERVALL_ERROR);
        }
    }

    private void handle(StatsborgerskapDTO statsborgerskap, PersonDTO person, InnflyttingDTO innflytting) {

        if (isBlank(statsborgerskap.getLandkode())) {
            if (nonNull(innflytting)) {
                statsborgerskap.setLandkode(innflytting.getFraflyttingsland());
            } else if (FNR.equals(IdenttypeUtility.getIdenttype(person.getIdent()))) {
                statsborgerskap.setLandkode(NORGE);
            } else {
                statsborgerskap.setLandkode(kodeverkConsumer.getTilfeldigLand().block());
            }
        }

        if (isNull(statsborgerskap.getGyldigFraOgMed()) &&
                isNull(statsborgerskap.getBekreftelsesdato()) &&
                statsborgerskap.getMaster() == DbVersjonDTO.Master.PDL) {
            statsborgerskap.setGyldigFraOgMed(FoedselsdatoUtility.getFoedselsdato(person));
        }
    }
}