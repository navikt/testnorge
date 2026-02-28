package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
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
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class InnflyttingService implements Validation<InnflyttingDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland";

    private final KodeverkConsumer kodeverkConsumer;
    private final BostedAdresseService bostedAdresseService;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getInnflytting())
                    .filter(type -> isTrue(type.getIsNew()))
                    .flatMap(type -> handle(type, person))
                    .doOnNext(type -> {
                        type.setKilde(getKilde(type));
                        type.setMaster(getMaster(type, person));
                    })
                    .collectList()
                    .then();
    }

    @Override
    public Mono<Void> validate(InnflyttingDTO innflytting) {

        if (isNotBlank(innflytting.getFraflyttingsland()) && !hasLandkode(innflytting.getFraflyttingsland())) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ERROR);
        }
        return Mono.empty();
    }

    protected Mono<InnflyttingDTO> handle(InnflyttingDTO innflytting, PersonDTO person) {

        return getInnflyttingDTO(innflytting, person)
                .doOnNext(innflytting1 -> {

                    if (person.getFolkeregisterPersonstatus().stream()
                            .filter(folkeregisterPersonstatus -> isNull(folkeregisterPersonstatus.getStatus()) ||
                                                                 BOSATT == folkeregisterPersonstatus.getStatus())
                            .filter(folkeregisterPersonstatus -> isNull(folkeregisterPersonstatus.getGyldigFraOgMed()) ||
                                                                 folkeregisterPersonstatus.getGyldigFraOgMed().equals(innflytting1.getInnflyttingsdato()))
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

    private Mono<InnflyttingDTO> getInnflyttingDTO(InnflyttingDTO innflytting, PersonDTO person) {

        return Mono.just(innflytting)
                .flatMap(innflytting1 ->
                        isBlank(innflytting1.getFraflyttingsland()) ?
                                kodeverkConsumer.getTilfeldigLand()
                                        .doOnNext(innflytting1::setFraflyttingsland)
                                        .thenReturn(innflytting1) : Mono.just(innflytting1))
                .flatMap(innflytting2 -> {

                    if (isNull(innflytting2.getInnflyttingsdato())) {
                        innflytting2.setInnflyttingsdato(LocalDateTime.now());
                    }

                    if (person.getBostedsadresse().stream()
                            .filter(BostedadresseDTO::isAdresseNorge)
                            .filter(adresse -> isNull(adresse.getGyldigTilOgMed()) ||
                                               adresse.getGyldigTilOgMed().isAfter(innflytting2.getInnflyttingsdato()))
                            .findFirst()
                            .isEmpty()) {

                        person.getBostedsadresse().addFirst(BostedadresseDTO.builder()
                                .vegadresse(new VegadresseDTO())
                                .gyldigFraOgMed(innflytting2.getInnflyttingsdato())
                                .isNew(true)
                                .id(person.getBostedsadresse().stream()
                                            .max(Comparator.comparing(BostedadresseDTO::getId))
                                            .orElse(BostedadresseDTO.builder().id(0).build())
                                            .getId() + 1)
                                .build()
                        );
                        return bostedAdresseService.convert(person, false)
                                .thenReturn(innflytting2);
                    }
                    return Mono.just(innflytting);
                });
    }
}
