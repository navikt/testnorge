package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.data.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.DOED;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FOEDSELSREGISTRERT;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.IKKE_BOSATT;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.INAKTIV;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.FNR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.NPID;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService implements BiValidation<FolkeregisterPersonstatusDTO, PersonDTO> {

    public List<FolkeregisterPersonstatusDTO> convert(PersonDTO person) {

        var touched = new AtomicBoolean(false);

        person.getFolkeregisterPersonstatus()
                .forEach(status -> {

                    if (isTrue(status.getIsNew())) {

                        handle(status, person);
                        status.setKilde(getKilde(status));
                        status.setMaster(getMaster(status, person));
                        touched.set(true);
                    }
                });

        if (person.getFolkeregisterPersonstatus().isEmpty() &&
                !person.getFalskIdentitet().isEmpty() &&
                person.getIdenttype() != NPID &&
                !isTestnorgeIdent(person.getIdent())) {

            person.getFolkeregisterPersonstatus().add(handle(FolkeregisterPersonstatusDTO.builder()
                            .id(1)
                            .kilde("Dolly")
                            .master(Master.FREG)
                            .build(),
                    person)
            );
            touched.set(true);
        }

        if (!touched.get() && !person.getFolkeregisterPersonstatus().isEmpty()) {

            handle(person.getFolkeregisterPersonstatus().getFirst(), person);
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
            personstatus.setGyldigFraOgMed(person.getDoedsfall().stream()
                    .map(DoedsfallDTO::getDoedsdato)
                    .findFirst()
                    .orElse(LocalDateTime.now()));

        } else if (person.getFalskIdentitet().stream().anyMatch(FalskIdentitetDTO::isFalskIdentitet)) {

            personstatus.setStatus(OPPHOERT);
            personstatus.setGyldigFraOgMed(person.getFalskIdentitet().stream()
                    .filter(FalskIdentitetDTO::isFalskIdentitet)
                    .map(FalskIdentitetDTO::getGyldigFraOgMed)
                    .findFirst()
                    .orElse(LocalDateTime.now()));

        } else if (!person.getUtflytting().isEmpty() && person.getUtflytting().stream()
                .noneMatch(utflytting -> person.getInnflytting().stream()
                        .anyMatch(innflytting ->
                                innflytting.getInnflyttingsdato().isAfter(utflytting.getUtflyttingsdato())))) {

            personstatus.setStatus(UTFLYTTET);
            personstatus.setGyldigFraOgMed(person.getUtflytting().stream()
                    .map(UtflyttingDTO::getUtflyttingsdato)
                    .findFirst()
                    .orElse(LocalDateTime.now()));

        } else if (!person.getOpphold().isEmpty() || DNR == getIdenttype(person.getIdent())) {

            personstatus.setStatus(MIDLERTIDIG);

        } else if (!person.getBostedsadresse().isEmpty()) {

            if (person.getBostedsadresse().getFirst().isAdresseUtland()) {
                personstatus.setStatus(IKKE_BOSATT);

            } else if (nonNull(person.getBostedsadresse().getFirst().getUkjentBosted())) {
                personstatus.setStatus(FOEDSELSREGISTRERT);

            } else {
                personstatus.setStatus(BOSATT);
            }

            personstatus.setGyldigFraOgMed(person.getBostedsadresse().stream()
                    .map(BostedadresseDTO::getGyldigFraOgMed)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(FoedselsdatoUtility.getFoedselsdato(person)));

        } else if (FNR != getIdenttype(person.getIdent()) &&
                person.getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()).countAdresser() == 0 ||
                nonNull(person.getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()).getUtenlandskAdresse())) {

            personstatus.setStatus(INAKTIV);
            personstatus.setGyldigFraOgMed(person.getBostedsadresse().stream()
                    .map(BostedadresseDTO::getGyldigFraOgMed)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(FoedselsdatoUtility.getFoedselsdato(person)));

        } else {

            personstatus.setStatus(FOEDSELSREGISTRERT);
            personstatus.setGyldigFraOgMed(FoedselsdatoUtility.getFoedselsdato(person));
        }

        return personstatus;
    }

    @Override
    public void validate(FolkeregisterPersonstatusDTO artifact, PersonDTO person) {

        // Ingen validering
    }
}