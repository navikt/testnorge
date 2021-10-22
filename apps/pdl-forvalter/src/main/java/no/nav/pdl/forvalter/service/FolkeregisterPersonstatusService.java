package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility.getIdenttype;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.DOED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.FOEDSELSREGISTRERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.IKKE_BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.UTFLYTTET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService implements BiValidation<FolkeregisterpersonstatusDTO, PersonDTO> {

    public List<FolkeregisterpersonstatusDTO> convert(PersonDTO person) {

        if (person.getFolkeregisterpersonstatus().stream()
                .anyMatch(personstatus -> isTrue(personstatus.getIsNew()))) {

            var personstatus = person.getFolkeregisterpersonstatus().stream().findFirst().get();
            personstatus.setStatus(getPersonstatus(person));
            personstatus.setKilde(isNotBlank(personstatus.getKilde()) ? personstatus.getKilde() : "Dolly");
            personstatus.setMaster(nonNull(personstatus.getMaster()) ? personstatus.getMaster() : Master.FREG);
            personstatus.setGjeldende(nonNull(personstatus.getGjeldende()) ? personstatus.getGjeldende(): true);

        } else if (person.getFolkeregisterpersonstatus().isEmpty() ||
                getPersonstatus(person) != person.getFolkeregisterpersonstatus().stream()
                        .findFirst().orElse(new FolkeregisterpersonstatusDTO()).getStatus()) {

            person.getFolkeregisterpersonstatus().add(0,
                    FolkeregisterpersonstatusDTO.builder()
                            .status(getPersonstatus(person))
                            .id(person.getFolkeregisterpersonstatus().stream()
                                    .map(FolkeregisterpersonstatusDTO::getId)
                                    .findFirst().orElse(0) + 1)
                            .kilde("Dolly")
                            .master(Master.FREG)
                            .gjeldende(true)
                            .build());
        }

        return person.getFolkeregisterpersonstatus();
    }

    private FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus getPersonstatus(PersonDTO person) {

        if (person.getFolkeregisterpersonstatus()
                .stream().anyMatch(status -> isTrue(status.getIsNew()) && nonNull(status.getStatus()))) {

            return person.getFolkeregisterpersonstatus().stream()
                    .filter(status -> isTrue(status.getIsNew()))
                    .map(FolkeregisterpersonstatusDTO::getStatus)
                    .findFirst().get();

        } else if (!person.getDoedsfall().isEmpty()) {

            return DOED;

        } else if (!person.getFalskIdentitet().isEmpty() && person.getFalskIdentitet().stream()
                .anyMatch(identitet -> isTrue(identitet.getErFalsk()) && isTrue(identitet.getGjeldende()))) {

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

            return IKKE_BOSATT;

        } else {

            return FOEDSELSREGISTRERT;
        }
    }

    @Override
    public void validate(FolkeregisterpersonstatusDTO artifact, PersonDTO person) {

        // Ingen validering
    }
}