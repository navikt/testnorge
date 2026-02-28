package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class DoedsfallService implements Validation<DoedsfallDTO> {

    private static final String INVALID_DATO_MISSING = "Dødsfall: dødsdato må oppgis";
    private final PersonRepository personRepository;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getDoedsfall())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .map(doedsfall -> {
                    doedsfall.sort(Comparator.comparing(DoedsfallDTO::getDoedsdato).reversed());
                    renumberId(doedsfall);
                    return doedsfall;
                })
                .doOnNext(doedsfall -> person.setDoedsfall(new java.util.ArrayList<>(doedsfall)))
                .then();
    }

    private Mono<DoedsfallDTO> handle(DoedsfallDTO doedsfall, PersonDTO person) {

        if (!person.getDoedsfall().isEmpty() &&
                !person.getSivilstand().isEmpty() &&
                isNotBlank(person.getSivilstand().getFirst().getRelatertVedSivilstand()) &&
                person.getSivilstand().getFirst().isGift()) {

            return personRepository.findByIdent(person.getSivilstand().getFirst().getRelatertVedSivilstand())
                    .doOnNext(person1 ->
                            person1.getPerson().getSivilstand().addFirst(
                                    SivilstandDTO.builder()
                                            .type(person.getSivilstand().getFirst().getGjenlevendeSivilstand())
                                            .sivilstandsdato(person.getDoedsfall().getFirst().getDoedsdato())
                                            .kilde(person.getDoedsfall().getFirst().getKilde())
                                            .master(person.getDoedsfall().getFirst().getMaster())
                                            .id(person1.getPerson().getSivilstand().size() + 1)
                                            .build()))
                    .flatMap(personRepository::save)
                    .thenReturn(doedsfall);
        }
        return Mono.just(doedsfall);
    }

    @Override
    public Mono<Void> validate(DoedsfallDTO type) {

        if (isNull(type.getDoedsdato())) {

            throw new InvalidRequestException(INVALID_DATO_MISSING);
        }
        return Mono.empty();
    }
}
