package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.ArtifactUtils;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.DOED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FOEDSELSREGISTRERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.IKKE_BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.NPID;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService implements BiValidation<FolkeregisterPersonstatusDTO, PersonDTO> {

    public List<FolkeregisterPersonstatusDTO> convert(PersonDTO person) {

        var touched = new AtomicBoolean(false);

        if (person.isNotChanged() || isTestnorgeIdent(person.getIdent()) || person.getIdenttype() == NPID) {
            return person.getFolkeregisterPersonstatus();
        }

        person.getFolkeregisterPersonstatus()
                .forEach(status -> {

                    if (isTrue(status.getIsNew())) {

                        handle(status, person);
                        status.setKilde(getKilde(status));
                        status.setMaster(getMaster(status, person));
                        touched.set(true);
                    }
                });

        if (!touched.get()) {

            var status = handle(FolkeregisterPersonstatusDTO.builder()
                    .id(person.getFolkeregisterPersonstatus().size() + 1)
                    .isNew(false)
                    .kilde("Dolly")
                    .master(Master.FREG)
                    .build(), person);

            if (nonNull(status.getStatus())) {
                person.getFolkeregisterPersonstatus().addFirst(status);
            }
        }

        setGyldigTilOgMed(person);
        return person.getFolkeregisterPersonstatus();
    }

    public List<FolkeregisterPersonstatusDTO> update(PersonDTO person) {

        person.setIsChanged(true);
        return convert(person);
    }

    private FolkeregisterPersonstatusDTO handle(FolkeregisterPersonstatusDTO personstatus, PersonDTO person) {

        if (person.getFolkeregisterPersonstatus()
                .stream().anyMatch(status -> isTrue(status.getIsNew()) && nonNull(status.getStatus()))) {

            return personstatus;

        } else if (!person.getDoedsfall().isEmpty()) {

            if (isNotCurrentStatus(DOED, person)) {
                personstatus.setStatus(DOED);
                personstatus.setGyldigFraOgMed(person.getDoedsfall().stream()
                        .map(DoedsfallDTO::getDoedsdato)
                        .findFirst()
                        .orElse(LocalDateTime.now()));
            }
            return personstatus;

        } else if (person.getFalskIdentitet().stream().anyMatch(FalskIdentitetDTO::isFalskIdentitet)) {

            if (isNotCurrentStatus(OPPHOERT, person)) {
                personstatus.setStatus(OPPHOERT);
                personstatus.setGyldigFraOgMed(person.getFalskIdentitet().stream()
                        .filter(FalskIdentitetDTO::isFalskIdentitet)
                        .map(FalskIdentitetDTO::getGyldigFraOgMed)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(LocalDateTime.now()));
            }
            return personstatus;

        } else if (!person.getUtflytting().isEmpty() && person.getUtflytting().stream()
                .noneMatch(utflytting -> person.getInnflytting().stream()
                        .anyMatch(innflytting ->
                                innflytting.getInnflyttingsdato().isAfter(utflytting.getUtflyttingsdato())))) {

            if (isNotCurrentStatus(UTFLYTTET, person)) {
                personstatus.setStatus(UTFLYTTET);
                personstatus.setGyldigFraOgMed(person.getUtflytting().stream()
                        .map(UtflyttingDTO::getUtflyttingsdato)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(LocalDateTime.now()));
            }
            return personstatus;

        } else if (!person.getOpphold().isEmpty() || DNR == getIdenttype(person.getIdent())) {

            if (isNotCurrentStatus(MIDLERTIDIG, person)) {
                personstatus.setStatus(MIDLERTIDIG);
                personstatus.setGyldigFraOgMed(FoedselsdatoUtility.getFoedselsdato(person));
            }
            return personstatus;

        } else if (!person.getBostedsadresse().isEmpty()) {

            if (person.getBostedsadresse().getFirst().isAdresseUtland()) {

                if (isNotCurrentStatus(IKKE_BOSATT, person)) {
                    personstatus.setStatus(IKKE_BOSATT);
                    personstatus.setGyldigFraOgMed(getBoadresseGyldigFraDato(person));
                }

            } else if (isNotCurrentStatus(BOSATT, person)) {

                personstatus.setStatus(BOSATT);
                personstatus.setGyldigFraOgMed(getBoadresseGyldigFraDato(person));
            }

            return personstatus;

        } else if (isNotCurrentStatus(FOEDSELSREGISTRERT, person)) {

            personstatus.setStatus(FOEDSELSREGISTRERT);
            personstatus.setGyldigFraOgMed(FoedselsdatoUtility.getFoedselsdato(person));
        }

        return personstatus;
    }

    private static boolean isNotCurrentStatus(FolkeregisterPersonstatus status, PersonDTO person) {

        return person.getFolkeregisterPersonstatus().isEmpty() ||
                person.getFolkeregisterPersonstatus().getFirst().getStatus() != status;
    }

    @Override
    public void validate(FolkeregisterPersonstatusDTO artifact, PersonDTO person) {

        // Ingen validering
    }

    private static LocalDateTime getBoadresseGyldigFraDato(PersonDTO person) {

        return person.getBostedsadresse().stream()
                .map(BostedadresseDTO::getGyldigFraOgMed)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(FoedselsdatoUtility.getFoedselsdato(person));
    }

    protected static void setGyldigTilOgMed(PersonDTO person) {

        var newStatus = new ArrayList<>(person
                .getFolkeregisterPersonstatus()
                .stream()
                .filter(status -> nonNull(status.getGyldigFraOgMed()))
                .sorted(Comparator
                        .comparing(FolkeregisterPersonstatusDTO::getGyldigFraOgMed)
                        .reversed())
                .toList());
        person.setFolkeregisterPersonstatus(newStatus);

        var folkeregisterPersonstatus = person.getFolkeregisterPersonstatus();
        ArtifactUtils.renumberId(folkeregisterPersonstatus);

        for (var i = folkeregisterPersonstatus.size() - 1; i >= 0; i--) {
            if (i - 1 >= 0) {
                fixGyldigTilOgMed(folkeregisterPersonstatus.get(i), folkeregisterPersonstatus.get(i - 1));
                fixGyldigFraOgMed(folkeregisterPersonstatus.get(i), folkeregisterPersonstatus.get(i - 1));
            }
        }

        if (!folkeregisterPersonstatus.isEmpty()) {
            folkeregisterPersonstatus.getFirst().setGyldigTilOgMed(null);
        }

    }

    private static void fixGyldigTilOgMed(FolkeregisterPersonstatusDTO statusA, FolkeregisterPersonstatusDTO statusB) {

        if (statusA.getGyldigFraOgMed().isEqual(statusB.getGyldigFraOgMed())) {
            statusA.setGyldigTilOgMed(statusB.getGyldigFraOgMed().plusDays(1));

        } else if (statusA.getGyldigFraOgMed().plusDays(1).isBefore(statusB.getGyldigFraOgMed())) {
            statusA.setGyldigTilOgMed(statusB.getGyldigFraOgMed().minusDays(1));

        } else {
            statusA.setGyldigTilOgMed(statusB.getGyldigFraOgMed());
        }
    }

    private static void fixGyldigFraOgMed(FolkeregisterPersonstatusDTO statusA, FolkeregisterPersonstatusDTO statusB) {

        if (statusA.getGyldigTilOgMed().isAfter(statusB.getGyldigFraOgMed()) ||
                statusA.getGyldigTilOgMed().isEqual(statusB.getGyldigFraOgMed())) {

            statusB.setGyldigFraOgMed(statusA.getGyldigTilOgMed().plusDays(1));
        }
    }
}