package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
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
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService implements BiValidation<FolkeregisterPersonstatusDTO, PersonDTO> {

    public List<FolkeregisterPersonstatusDTO> convert(PersonDTO person) {

        person.getFolkeregisterPersonstatus()
                .forEach(status -> {

                    if (isTrue(status.getIsNew())) {

                        handle(status, person);
                        status.setKilde(isNotBlank(status.getKilde()) ? status.getKilde() : "Dolly");
                        status.setMaster(nonNull(status.getMaster()) ? status.getMaster() : Master.FREG);
                    }
                });

        if (person.getFolkeregisterPersonstatus().isEmpty() && !person.getFalskIdentitet().isEmpty()) {
            person.getFolkeregisterPersonstatus().add(handle(FolkeregisterPersonstatusDTO.builder()
                            .id(1)
                            .kilde("Dolly")
                            .master(Master.FREG)
                            .build(),
                    person)
            );
        }

        return person.getFolkeregisterPersonstatus();
    }


    private FolkeregisterPersonstatusDTO handle(FolkeregisterPersonstatusDTO personstatus, PersonDTO person) {

        if (person.getFolkeregisterPersonstatus()
                .stream().anyMatch(status -> isTrue(status.getIsNew()) && nonNull(status.getStatus()))) {

            // Status allerede satt

        } else if (!person.getDoedsfall().isEmpty()) {

            personstatus.setStatus(DOED);
            personstatus.setGyldigFraOgMed(person.getDoedsfall().stream().findFirst().orElse(new DoedsfallDTO())
                    .getDoedsdato());

        } else if (!person.getFalskIdentitet().isEmpty() && person.getFalskIdentitet().stream()
                .anyMatch(FalskIdentitetDTO::isFalskIdentitet)) {

            personstatus.setStatus(OPPHOERT);
            personstatus.setGyldigFraOgMed(person.getFalskIdentitet().stream()
                    .filter(FalskIdentitetDTO::isFalskIdentitet)
                    .findFirst().orElse(new FalskIdentitetDTO())
                    .getGyldigFraOgMed());

        } else if (!person.getUtflytting().isEmpty() && person.getInnflytting().isEmpty() ||
                !person.getUtflytting().isEmpty() && !person.getInnflytting().isEmpty() &&
                        person.getUtflytting().stream().findFirst().get().getUtflyttingsdato().isAfter(
                                person.getInnflytting().stream().findFirst().get().getInnflyttingsdato())) {

            personstatus.setStatus(UTFLYTTET);
            personstatus.setGyldigFraOgMed(person.getUtflytting().stream()
                    .findFirst().orElse(new UtflyttingDTO())
                    .getUtflyttingsdato());

        } else if (!person.getOpphold().isEmpty() || DNR == getIdenttype(person.getIdent())) {

            personstatus.setStatus(MIDLERTIDIG);

        } else if (!person.getBostedsadresse().isEmpty() &&
                (person.getBostedsadresse().stream().findFirst().get().countAdresser() == 0 ||
                        (nonNull(person.getBostedsadresse().stream().findFirst().get().getVegadresse()) ||
                                nonNull(person.getBostedsadresse().stream().findFirst().get().getMatrikkeladresse())))) {

            personstatus.setStatus(BOSATT);
            personstatus.setGyldigFraOgMed(person.getBostedsadresse().stream()
                    .findFirst().orElse(new BostedadresseDTO())
                    .getGyldigFraOgMed());

        } else if (FNR != getIdenttype(person.getIdent()) &&
                person.getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()).countAdresser() == 0 ||
                nonNull(person.getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()).getUtenlandskAdresse())) {

            personstatus.setStatus(INAKTIV);
            personstatus.setGyldigFraOgMed(person.getBostedsadresse().stream()
                    .findFirst().orElse(new BostedadresseDTO())
                    .getGyldigFraOgMed());

        } else {

            personstatus.setStatus(FOEDSELSREGISTRERT);
            personstatus.setGyldigFraOgMed(person.getFoedsel().stream()
                    .findFirst().orElse(new FoedselDTO())
                    .getFoedselsdato());
        }

        return personstatus;
    }

    @Override
    public void validate(FolkeregisterPersonstatusDTO artifact, PersonDTO person) {

        // Ingen validering
    }
}