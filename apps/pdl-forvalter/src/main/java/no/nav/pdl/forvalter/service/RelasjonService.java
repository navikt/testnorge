package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class RelasjonService {

    private static final String DB_ERROR = "Person %s ikke funnet i database";

    private final RelasjonRepository relasjonRepository;
    private final PersonRepository personRepository;

    public void setRelasjoner(String ident, RelasjonType relasjon, String identRelasjon, RelasjonType reverseRelasjon) {

        var personIder = personRepository.findByIdentIn(List.of(ident, identRelasjon))
                .collectList()
                .block()
                .stream()
                .collect(Collectors.toMap(DbPerson::getIdent, DbPerson::getId));

        createRelasjon(personIder.get(identRelasjon), personIder.get(ident), relasjon);

        if (nonNull(reverseRelasjon)) {
            createRelasjon(personIder.get(ident), personIder.get(identRelasjon), reverseRelasjon);
        }
    }

    public void setRelasjon(String ident, String identRelasjon, RelasjonType relasjon) {

       setRelasjoner(identRelasjon, relasjon, ident, null);
    }

    private Mono<DbRelasjon> createRelasjon(Long person1Id, Long person2Id, RelasjonType relasjon) {

        return relasjonRepository.save(DbRelasjon.builder()
                .personId(person1Id)
                .relatertPersonId(person2Id)
                .relasjonType(relasjon)
                .sistOppdatert(LocalDateTime.now())
                .build());
    }
}
