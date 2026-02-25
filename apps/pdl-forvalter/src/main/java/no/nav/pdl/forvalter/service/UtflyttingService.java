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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

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

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getUtflytting())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(utflytting -> person.setUtflytting(new ArrayList<>(utflytting)))
                .then();
    }

    @Override
    public Mono<Void> validate(UtflyttingDTO utflytting) {

        if (isNotBlank(utflytting.getTilflyttingsland()) && !hasLandkode(utflytting.getTilflyttingsland())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }
        return Mono.empty();
    }

    protected Mono<UtflyttingDTO> handle(UtflyttingDTO utflytting, PersonDTO person) {

        return getUtflytting(utflytting, person)
                .doOnNext(utflytting1 -> {
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
                });
    }

    private Mono<UtflyttingDTO> getUtflytting(UtflyttingDTO utflytting, PersonDTO person) {


        return Mono.just(utflytting)
                .flatMap(utflytting1 ->
                        isBlank(utflytting1.getTilflyttingsland()) ?
                                kodeverkConsumer.getTilfeldigLand()
                                        .doOnNext(utflytting1::setTilflyttingsland)
                                        .thenReturn(utflytting1) :
                                Mono.just(utflytting1))
                .flatMap(utflytting2 -> {

                    if (isNull(utflytting2.getUtflyttingsdato())) {
                        utflytting2.setUtflyttingsdato(LocalDateTime.now());
                    }

                    person.getBostedsadresse()
                            .removeIf(bostedsadresse -> bostedsadresse.isAdresseNorge() &&
                                                        bostedsadresse.getGyldigFraOgMed().isAfter(utflytting2.getUtflyttingsdato()));

                    if (!person.getBostedsadresse().isEmpty() && person.getBostedsadresse().getFirst().isAdresseNorge()) {
                        person.getBostedsadresse().getFirst().setGyldigTilOgMed(utflytting2.getUtflyttingsdato().minusDays(1));
                    }

                    if (utflytting2.isVelkjentLand() && person.getBostedsadresse().stream()
                            .filter(BostedadresseDTO::isAdresseUtland)
                            .filter(adresse -> isNull(adresse.getGyldigTilOgMed()) ||
                                               adresse.getGyldigTilOgMed().isAfter(utflytting2.getUtflyttingsdato()))
                            .findFirst()
                            .isEmpty() && person.getKontaktadresse().stream()
                                .filter(KontaktadresseDTO::isAdresseUtland)
                                .filter(adresse -> isNull(adresse.getGyldigTilOgMed()) ||
                                                   adresse.getGyldigTilOgMed().isAfter(utflytting2.getUtflyttingsdato()))
                                .findFirst()
                                .isEmpty()) {

                        person.getKontaktadresse().addFirst(KontaktadresseDTO.builder()
                                .utenlandskAdresse(UtenlandskAdresseDTO.builder()
                                        .landkode(utflytting2.getTilflyttingsland())
                                        .build())
                                .gyldigFraOgMed(utflytting2.getUtflyttingsdato())
                                .isNew(true)
                                .id(person.getKontaktadresse().stream()
                                            .max(Comparator.comparing(KontaktadresseDTO::getId))
                                            .orElse(KontaktadresseDTO.builder().id(0).build())
                                            .getId() + 1)
                                .build());

                        return kontaktAdresseService.convert(person, false)
                                .thenReturn(utflytting2);
                    }
                    return Mono.just(utflytting2);
                });
    }
}