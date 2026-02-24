package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.hasLandkode;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UtflyttingService implements Validation<UtflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland";

    private final KodeverkConsumer kodeverkConsumer;
    private final KontaktAdresseService kontaktAdresseService;

    public List<UtflyttingDTO> convert(PersonDTO person) {

        for (var type : person.getUtflytting()) {
            if (isTrue(type.getIsNew())) {

                handle(type, person);
                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }
        return person.getUtflytting();
    }

    @Override
    public void validate(UtflyttingDTO utflytting) {

        if (isNotBlank(utflytting.getTilflyttingsland()) && !hasLandkode(utflytting.getTilflyttingsland())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }
    }

    protected void handle(UtflyttingDTO utflytting, PersonDTO person) {

        if (isBlank(utflytting.getTilflyttingsland())) {
            utflytting.setTilflyttingsland(kodeverkConsumer.getTilfeldigLand());
        }

        if (isNull(utflytting.getUtflyttingsdato())) {
            utflytting.setUtflyttingsdato(LocalDateTime.now());
        }

        person.getBostedsadresse()
                .removeIf(bostedsadresse -> bostedsadresse.isAdresseNorge() &&
                        bostedsadresse.getGyldigFraOgMed().isAfter(utflytting.getUtflyttingsdato()));

        if (!person.getBostedsadresse().isEmpty() && person.getBostedsadresse().getFirst().isAdresseNorge()) {
            person.getBostedsadresse().getFirst().setGyldigTilOgMed(utflytting.getUtflyttingsdato().minusDays(1));
        }

        if (utflytting.isVelkjentLand() && person.getBostedsadresse().stream()
                .filter(BostedadresseDTO::isAdresseUtland)
                .filter(adresse -> isNull(adresse.getGyldigTilOgMed()) ||
                        adresse.getGyldigTilOgMed().isAfter(utflytting.getUtflyttingsdato()))
                .findFirst()
                .isEmpty() && person.getKontaktadresse().stream()
                .filter(KontaktadresseDTO::isAdresseUtland)
                .filter(adresse -> isNull(adresse.getGyldigTilOgMed()) ||
                        adresse.getGyldigTilOgMed().isAfter(utflytting.getUtflyttingsdato()))
                .findFirst()
                .isEmpty()) {

            person.getKontaktadresse().addFirst(KontaktadresseDTO.builder()
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
                .filter(folkeregisterPersonstatus -> isNull(folkeregisterPersonstatus.getStatus()) ||
                        UTFLYTTET == folkeregisterPersonstatus.getStatus())
                .filter(folkeregisterPersonstatus -> isNull(folkeregisterPersonstatus.getGyldigFraOgMed()) ||
                        folkeregisterPersonstatus.getGyldigFraOgMed().equals(utflytting.getUtflyttingsdato()))
                .findFirst()
                .isEmpty()) {

            person.getFolkeregisterPersonstatus().addFirst(FolkeregisterPersonstatusDTO.builder()
                    .isNew(true)
                    .id(person.getFolkeregisterPersonstatus().stream()
                            .max(Comparator.comparing(FolkeregisterPersonstatusDTO::getId))
                            .orElse(FolkeregisterPersonstatusDTO.builder().id(0).build())
                            .getId() + 1)
                    .build());
        }
    }
}