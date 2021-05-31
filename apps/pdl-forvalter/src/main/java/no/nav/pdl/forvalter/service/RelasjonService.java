package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.model.RelasjonType;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
                .findFirst().orElseThrow(() -> new HttpClientErrorException(INTERNAL_SERVER_ERROR,
                        String.format(DB_ERROR, ident)));
        var relasjonPerson = dbPersoner.stream()
                .filter(person -> person.getIdent().equals(identRelasjon))
                .findFirst().orElseThrow(() -> new HttpClientErrorException(INTERNAL_SERVER_ERROR,
                        String.format(DB_ERROR, identRelasjon)));

        createRelasjon(hovedperson, relasjonPerson, relasjon);
        createRelasjon(relasjonPerson, hovedperson, reverseRelasjon);
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
