package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class SikkerhetstiltakService implements Validation<SikkerhetstiltakDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Sikkerhetstiltak: Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_GYLDIGFOM_ERROR = "Sikkerhetstiltak: GyldigFom må angis";
    private static final String VALIDATION_GYLDIGTOM_ERROR = "Sikkerhetstiltak: GyldigTom må angis";
    private static final String VALIDATION_TILTAKSTYPE_ERROR = "Sikkerhetstiltak: Tiltakstype må angis";
    private static final String VALIDATION_BESKRIVELSE_ERROR = "Sikkerhetstiltak: Beskrivelse må angis";
    private static final String VALIDATION_KONTAKTPERSON_ERROR = "Sikkerhetstiltak: Personident og enhet må angis";
    private static final String VALIDATION_PERSONIDENT_ERROR = "Sikkerhetstiltak: NAV personident må angis";
    private static final String VALIDATION_ENHET_ERROR = "Sikkerhetstiltak: Enhet må angis";

    public List<SikkerhetstiltakDTO> convert(PersonDTO person) {

        for (var type : person.getSikkerhetstiltak()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(Master.PDL);
            }
        }

        person.setSikkerhetstiltak(new ArrayList<>(person.getSikkerhetstiltak()));
        person.getSikkerhetstiltak().sort(Comparator.comparing(SikkerhetstiltakDTO::getGyldigFraOgMed).reversed());
        renumberId(person.getSikkerhetstiltak());

        return person.getSikkerhetstiltak();
    }

    public void validate(SikkerhetstiltakDTO sikkerhetstiltak) {

        if (isNull(sikkerhetstiltak.getTiltakstype())) {
            throw new InvalidRequestException(VALIDATION_TILTAKSTYPE_ERROR);
        }

        if (isNull(sikkerhetstiltak.getBeskrivelse())) {
            throw new InvalidRequestException(VALIDATION_BESKRIVELSE_ERROR);
        }

        if (isNull(sikkerhetstiltak.getGyldigFraOgMed())) {
            throw new InvalidRequestException(VALIDATION_GYLDIGFOM_ERROR);
        }

        if (isNull(sikkerhetstiltak.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_GYLDIGTOM_ERROR);
        }

        if (sikkerhetstiltak.getGyldigFraOgMed().isAfter(sikkerhetstiltak.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (isNull(sikkerhetstiltak.getKontaktperson())) {
            throw new InvalidRequestException(VALIDATION_KONTAKTPERSON_ERROR);
        }

        if (isBlank(sikkerhetstiltak.getKontaktperson().getPersonident())) {
            throw new InvalidRequestException(VALIDATION_PERSONIDENT_ERROR);
        }

        if (nonNull(sikkerhetstiltak.getKontaktperson()) && isBlank(sikkerhetstiltak.getKontaktperson().getEnhet())) {
            throw new InvalidRequestException(VALIDATION_ENHET_ERROR);
        }
    }
}