package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GeografiskeKodeverkConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UtflyttingService implements Validation<UtflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland";
    private static final String LANDKODE_UKJENT = "XUK";

    private final GeografiskeKodeverkConsumer geografiskeKodeverkConsumer;
    private final KontaktAdresseService kontaktAdresseService;

    public List<UtflyttingDTO> convert(PersonDTO person) {

        for (var type : person.getUtflytting()) {
            if (isTrue(type.getIsNew())) {

                handle(type, person);
                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
            }
        }
        return person.getUtflytting();
    }

    @Override
    public void validate(UtflyttingDTO utflytting) {

        if (isNotBlank(utflytting.getTilflyttingsland()) && !isLandkode(utflytting.getTilflyttingsland())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }
    }

    protected void handle(UtflyttingDTO utflytting, PersonDTO person) {

        if (isBlank(utflytting.getTilflyttingsland())) {
            utflytting.setTilflyttingsland(geografiskeKodeverkConsumer.getTilfeldigLand());
        }

        if (isNull(utflytting.getUtflyttingsdato())) {
            utflytting.setUtflyttingsdato(LocalDateTime.now());
        }

        if (!person.getBostedsadresse().isEmpty() && person.getBostedsadresse().get(0).isAdresseNorge()) {
            person.getBostedsadresse().get(0).setGyldigTilOgMed(utflytting.getUtflyttingsdato().minusDays(1));
        }

        if (!person.getBostedsadresse().stream()
                .findFirst()
                .orElse(new BostedadresseDTO())
                .isAdresseUtland() &&
                !person.getKontaktadresse().stream()
                        .findFirst()
                        .orElse(new KontaktadresseDTO())
                        .isAdresseUtland() &&
                !LANDKODE_UKJENT.equals(utflytting.getTilflyttingsland())) {

            person.getKontaktadresse().add(0, KontaktadresseDTO.builder()
                    .utenlandskAdresse(UtenlandskAdresseDTO.builder()
                            .landkode(utflytting.getTilflyttingsland())
                            .build())
                    .gyldigFraOgMed(utflytting.getUtflyttingsdato())
                    .isNew(true)
                    .id(person.getKontaktadresse().stream()
                            .max(Comparator.comparing(KontaktadresseDTO::getId))
                            .orElse(KontaktadresseDTO.builder().id(0).build())
                            .getId() + 1)
                    .build()
            );
            kontaktAdresseService.convert(person, false);
        }

        if (person.getFolkeregisterPersonstatus().stream()
                .findFirst()
                .orElse(new FolkeregisterPersonstatusDTO())
                .getStatus() != FolkeregisterPersonstatus.UTFLYTTET ||
                isNotTrue(person.getFolkeregisterPersonstatus().stream()
                        .findFirst()
                        .orElse(new FolkeregisterPersonstatusDTO())
                        .getIsNew())) {

            person.getFolkeregisterPersonstatus().add(0, FolkeregisterPersonstatusDTO.builder()
                    .isNew(true)
                    .id(person.getFolkeregisterPersonstatus().stream()
                            .max(Comparator.comparing(FolkeregisterPersonstatusDTO::getId))
                            .orElse(FolkeregisterPersonstatusDTO.builder().id(0).build())
                            .getId() + 1)
                    .build());
        }
    }
}