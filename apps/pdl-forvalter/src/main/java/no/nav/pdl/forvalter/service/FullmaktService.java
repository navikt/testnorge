package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FullmaktService implements BiValidation<FullmaktDTO, PersonDTO> {

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getFullmakt())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person.getIdent()))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(fullmakt -> person.setFullmakt(new ArrayList<>(fullmakt)))
                .then();
    }

    @Override
    public Mono<Void> validate(FullmaktDTO fullmakt, PersonDTO person) {

        return Mono.empty();
    }

    private Mono<FullmaktDTO> handle(FullmaktDTO fullmakt, String ident) {

        return getFullmaktPerson(fullmakt, ident)
                .flatMap(fm -> relasjonService.setRelasjoner(ident, RelasjonType.FULLMAKTSGIVER,
                        fm.getMotpartsPersonident(), RelasjonType.FULLMEKTIG).thenReturn(fm))
                .doOnNext(fm -> fm.setMaster(Master.PDL));
    }

    private Mono<FullmaktDTO> getFullmaktPerson(FullmaktDTO fullmakt, String ident) {

        fullmakt.setEksisterendePerson(isNotBlank(fullmakt.getMotpartsPersonident()));

        if (isFalse(fullmakt.getEksisterendePerson())) {

            if (isNull(fullmakt.getNyFullmektig())) {
                fullmakt.setNyFullmektig(new PersonRequestDTO());
            }

            if (isNull(fullmakt.getNyFullmektig().getAlder()) &&
                isNull(fullmakt.getNyFullmektig().getFoedtEtter()) &&
                isNull(fullmakt.getNyFullmektig().getFoedtFoer())) {

                fullmakt.getNyFullmektig().setFoedtFoer(LocalDateTime.now().minusYears(18));
                fullmakt.getNyFullmektig().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            EgenskaperFraHovedperson.kopierData(ident, fullmakt.getNyFullmektig());

            return createPersonService.execute(fullmakt.getNyFullmektig())
                    .map(PersonDTO::getIdent)
                    .doOnNext(fullmakt::setMotpartsPersonident)
                    .thenReturn(fullmakt);
        } else {

            return personRepository.findByIdent(fullmakt.getMotpartsPersonident())
                    .switchIfEmpty(Mono.just(DbPerson.builder()
                                    .ident(fullmakt.getMotpartsPersonident())
                                    .person(PersonDTO.builder()
                                            .ident(fullmakt.getMotpartsPersonident())
                                            .build())
                                    .sistOppdatert(LocalDateTime.now())
                                    .build())
                            .flatMap(personRepository::save))
                    .map(DbPerson::getIdent)
                    .doOnNext(fullmakt::setMotpartsPersonident)
                    .thenReturn(fullmakt);
        }
    }
}