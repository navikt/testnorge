package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelasjonService {

    private static final String DB_ERROR = "Person %s ikke funnet i database";

    private final RelasjonRepository relasjonRepository;
    private final PersonRepository personRepository;

    public void setRelasjoner(String ident, RelasjonType relasjon, String identRelasjon, RelasjonType reverseRelasjon) {

        var dbPersoner = personRepository.findByIdentIn(List.of(ident, identRelasjon));
        var hovedperson = dbPersoner.stream()
                .filter(person -> person.getIdent().equals(ident))
                .findFirst().orElseThrow(() -> new InternalServerException(
                        String.format(DB_ERROR, ident)));
        var relasjonPerson = dbPersoner.stream()
                .filter(person -> person.getIdent().equals(identRelasjon))
                .findFirst().orElseThrow(() -> new InternalServerException(
                        String.format(DB_ERROR, identRelasjon)));

        createRelasjon(relasjonPerson, hovedperson, relasjon);
        createRelasjon(hovedperson, relasjonPerson, reverseRelasjon);
    }

    private void createRelasjon(DbPerson person1, DbPerson person2, RelasjonType relasjon) {

        relasjonRepository.save(DbRelasjon.builder()
                .person(person1)
                .relatertPerson(person2)
                .relasjonType(relasjon)
                .sistOppdatert(LocalDateTime.now())
                .build());
    }
}
