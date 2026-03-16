package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class RelasjonService {

    private final RelasjonRepository relasjonRepository;
    private final PersonRepository personRepository;

    public Mono<Void> setRelasjoner(String ident, RelasjonType relasjon, String identRelasjon, RelasjonType reverseRelasjon) {

        return Mono.zip(fetchPerson(ident),
                        fetchPerson(identRelasjon))
                .flatMap(personer ->
                        createRelasjoner(personer.getT1(), relasjon, personer.getT2(), reverseRelasjon))
                .then();
    }

    public Mono<Void> setRelasjon(String ident, String identRelasjon, RelasjonType relasjon) {

        return Mono.zip(fetchPerson(ident),
                        fetchPerson(identRelasjon))
                .flatMap(personer ->
                        checkAndCreateRelasjon(personer.getT1(), personer.getT2(), relasjon))
                .then();
    }

    private Mono<DbPerson> fetchPerson(String ident) {

        return personRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(String.format("Person med ident %s finnes ikke", ident))));
    }

    private Mono<DbRelasjon> checkAndCreateRelasjon(DbPerson person1, DbPerson person2, RelasjonType relasjonType) {

        return relasjonRepository.findByPersonIdAndRelatertPersonIdAndRelasjonType(person1.getId(), person2.getId(), relasjonType)
                .switchIfEmpty(Mono.just(DbRelasjon.builder()
                        .personId(person1.getId())
                        .relatertPersonId(person2.getId())
                        .relasjonType(relasjonType)
                        .sistOppdatert(now())
                        .build()))
                .flatMap(relasjonRepository::save);
    }

    private Mono<Void> createRelasjoner(DbPerson person1, RelasjonType relasjon,
                                              DbPerson person2, RelasjonType reverseRelasjon) {

        return Mono.just(List.of(
                        DbRelasjon.builder()
                                .personId(person1.getId())
                                .person(person1)
                                .relatertPersonId(person2.getId())
                                .relatertPerson(person2)
                                .relasjonType(relasjon)
                                .sistOppdatert(now())
                                .build(),
                        DbRelasjon.builder()
                                .personId(person2.getId())
                                .person(person2)
                                .relatertPersonId(person1.getId())
                                .relatertPerson(person1)
                                .relasjonType(reverseRelasjon)
                                .sistOppdatert(now())
                                .build()))
                .flatMapMany(relasjonRepository::saveAll)
                .then();
    }
}
