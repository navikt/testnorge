package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class RelasjonService {

    private final RelasjonRepository relasjonRepository;
    private final PersonRepository personRepository;

    public Mono<Void> setRelasjoner(String ident, RelasjonType relasjon, String identRelasjon, RelasjonType reverseRelasjon) {

        return checkAndCreateMissingPerson(ident)
                .flatMap(person1 -> checkAndCreateMissingPerson(identRelasjon)
                        .flatMap(person2 -> checkAndCreateRelasjon(person1, person2, relasjon)
                                .then(nonNull(reverseRelasjon) ?
                                        checkAndCreateRelasjon(person2, person1, reverseRelasjon) :
                                        Mono.empty())))
                .then();
    }

    private Mono<DbPerson> checkAndCreateMissingPerson(String ident) {

        return personRepository.findByIdent(ident)
                .switchIfEmpty(Mono.just(DbPerson.builder()
                                .ident(ident)
                                .person(PersonDTO.builder()
                                        .ident(ident)
                                        .build())
                                .sistOppdatert(now())
                                .build())
                        .flatMap(personRepository::save));
    }

    private Mono<DbRelasjon> checkAndCreateRelasjon(DbPerson person1, DbPerson person2, RelasjonType relasjonType) {

        return relasjonRepository.findByPersonIdAndRelatertPersonIdAndRelasjonType(person1.getId(), person2.getId(), relasjonType)
                .switchIfEmpty(Mono.just(DbRelasjon.builder()
                        .personId(person1.getId())
                        .relatertPersonId(person2.getId())
                        .relasjonType(relasjonType)
                        .sistOppdatert(now())
                        .build())
                        .flatMap(relasjonRepository::save));
    }
}
