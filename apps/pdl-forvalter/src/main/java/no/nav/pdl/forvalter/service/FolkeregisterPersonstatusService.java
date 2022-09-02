package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
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
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.IKKE_BOSATT;
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

    public List<FolkeregisterPersonstatusDTO> update(PersonDTO person) {

        person.getFolkeregisterPersonstatus().clear();
        person.getFolkeregisterPersonstatus()
                .add(FolkeregisterPersonstatusDTO.builder()
                        .id(1)
                        .isNew(true)
                        .build());

        return convert(person);
    }

    private FolkeregisterPersonstatusDTO handle(FolkeregisterPersonstatusDTO personstatus, PersonDTO person) {

        if (person.getFolkeregisterPersonstatus()
                .stream().anyMatch(status -> isTrue(status.getIsNew()) && nonNull(status.getStatus()))) {

            // Status allerede satt

        } else if (!person.getDoedsfall().isEmpty()) {

            personstatus.setStatus(DOED);
            personstatus.setGyldigFraOgMed(person.getDoedsfall().stream().findFirst().orElse(new DoedsfallDTO())
                    .getDoedsdato());

        } else if (person.getFalskIdentitet().stream().anyMatch(FalskIdentitetDTO::isFalskIdentitet)) {

            personstatus.setStatus(OPPHOERT);
            personstatus.setGyldigFraOgMed(person.getFalskIdentitet().stream()
                    .filter(FalskIdentitetDTO::isFalskIdentitet)
                    .findFirst().orElse(new FalskIdentitetDTO())
                    .getGyldigFraOgMed());

        } else if (!person.getUtflytting().isEmpty() && person.getUtflytting().stream()
                    .noneMatch(utflytting -> person.getInnflytting().stream()
                            .anyMatch(innflytting ->
                                    innflytting.getInnflyttingsdato().isAfter(utflytting.getUtflyttingsdato())))) {

            personstatus.setStatus(UTFLYTTET);
            personstatus.setGyldigFraOgMed(person.getUtflytting().stream()
                    .findFirst().orElse(new UtflyttingDTO())
                    .getUtflyttingsdato());

        } else if (!person.getOpphold().isEmpty() || DNR == getIdenttype(person.getIdent())) {

            personstatus.setStatus(MIDLERTIDIG);

        } else if (!person.getBostedsadresse().isEmpty()) {

            if (person.getBostedsadresse().get(0).isAdresseUtland()) {
                personstatus.setStatus(IKKE_BOSATT);

            } else if (nonNull(person.getBostedsadresse().get(0).getUkjentBosted())) {
                personstatus.setStatus(FOEDSELSREGISTRERT);

            } else {
                personstatus.setStatus(BOSATT);
            }

            personstatus.setGyldigFraOgMed(nonNull(person.getBostedsadresse().get(0).getGyldigFraOgMed()) ?
                    person.getBostedsadresse().get(0).getGyldigFraOgMed() :
                    DatoFraIdentUtility.getDato(person.getIdent()).atStartOfDay());

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