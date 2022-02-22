package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility.getIdenttype;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.DOED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FOEDSELSREGISTRERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.INAKTIV;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService implements BiValidation<FolkeregisterPersonstatusDTO, PersonDTO> {

    public List<FolkeregisterPersonstatusDTO> convert(PersonDTO person) {

        if (person.getFolkeregisterPersonstatus().stream()
                .anyMatch(personstatus -> isTrue(personstatus.getIsNew()))) {

            var personstatus = person.getFolkeregisterPersonstatus().stream().findFirst().get();
            personstatus.setStatus(getPersonstatus(person));
            personstatus.setKilde(isNotBlank(personstatus.getKilde()) ? personstatus.getKilde() : "Dolly");
            personstatus.setMaster(nonNull(personstatus.getMaster()) ? personstatus.getMaster() : Master.FREG);

        } else if (person.getFolkeregisterPersonstatus().isEmpty() && !person.getFalskIdentitet().isEmpty()) {

            person.getFolkeregisterPersonstatus().add(0,
                    FolkeregisterPersonstatusDTO.builder()
                            .status(getPersonstatus(person))
                            .id(person.getFolkeregisterPersonstatus().stream()
                                    .map(FolkeregisterPersonstatusDTO::getId)
                                    .findFirst().orElse(0) + 1)
                            .kilde("Dolly")
                            .master(Master.FREG)
                            .gjeldende(true)
                            .build());
        }

        return person.getFolkeregisterPersonstatus();
    }

    private FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus getPersonstatus(PersonDTO person) {

        if (person.getFolkeregisterPersonstatus()
                .stream().anyMatch(status -> isTrue(status.getIsNew()) && nonNull(status.getStatus()))) {

            return person.getFolkeregisterPersonstatus().stream()
                    .filter(status -> isTrue(status.getIsNew()))
                    .map(FolkeregisterPersonstatusDTO::getStatus)
                    .findFirst().get();

        } else if (!person.getDoedsfall().isEmpty()) {

            return DOED;

        } else if (!person.getFalskIdentitet().isEmpty() && person.getFalskIdentitet().stream()
                .anyMatch(identitet -> isTrue(identitet.getErFalsk()))) {

            return OPPHOERT;

        } else if (!person.getUtflytting().isEmpty() && person.getInnflytting().isEmpty() ||
                !person.getUtflytting().isEmpty() && !person.getInnflytting().isEmpty() &&
                        person.getUtflytting().stream().findFirst().get().getUtflyttingsdato().isAfter(
                                person.getInnflytting().stream().findFirst().get().getInnflyttingsdato())) {

            return UTFLYTTET;

        } else if (!person.getOpphold().isEmpty()) {

            return MIDLERTIDIG;

        } else if (!person.getBostedsadresse().isEmpty() &&
                (person.getBostedsadresse().stream().findFirst().get().countAdresser() == 0 ||
                        (nonNull(person.getBostedsadresse().stream().findFirst().get().getVegadresse()) ||
                                nonNull(person.getBostedsadresse().stream().findFirst().get().getMatrikkeladresse())))) {

            return BOSATT;

        } else if (FNR != getIdenttype(person.getIdent()) &&
                person.getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()).countAdresser() == 0 ||
                nonNull(person.getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()).getUtenlandskAdresse())) {

            return INAKTIV;

        } else {

            return FOEDSELSREGISTRERT;
        }
    }

    @Override
    public void validate(FolkeregisterPersonstatusDTO artifact, PersonDTO person) {

        // Ingen validering
    }
}