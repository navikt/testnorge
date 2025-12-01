package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;

@Service
@RequiredArgsConstructor
public class RelasjonService {

    private static final String DB_ERROR = "Person %s ikke funnet i database";

    private final RelasjonRepository relasjonRepository;
    private final PersonRepository personRepository;

    public void setRelasjoner(String ident, RelasjonType relasjon, String identRelasjon, RelasjonType reverseRelasjon) {

        var dbPersoner = checkAndCreateMissingPersons(ident, identRelasjon);

        var hovedperson = dbPersoner.stream()
                .filter(person -> person.getIdent().equals(ident))
                .findFirst().orElseThrow(() -> new InternalServerException(
                        String.format(DB_ERROR, ident)));
        var relasjonPerson = dbPersoner.stream()
                .filter(person -> person.getIdent().equals(identRelasjon))
                .findFirst().orElseThrow(() -> new InternalServerException(
                        String.format(DB_ERROR, identRelasjon)));

        createRelasjon(relasjonPerson, hovedperson, relasjon);

        if (nonNull(reverseRelasjon)) {
            createRelasjon(hovedperson, relasjonPerson, reverseRelasjon);
        }
    }

    private List<DbPerson> checkAndCreateMissingPersons(String ident, String identRelasjon) {

        var dbPersoner = personRepository.findByIdentIn(List.of(ident, identRelasjon),
                PageRequest.of(0, 10));

        Stream.of(ident, identRelasjon)
                .filter(id -> isTestnorgeIdent(id) &&
                        dbPersoner.stream().noneMatch(dbPerson -> dbPerson.getIdent().equals(id)))
                .forEach(id -> personRepository.save(DbPerson.builder()
                        .ident(id)
                        .person(PersonDTO.builder()
                                .ident(id)
                                .build())
                        .sistOppdatert(now())
                        .build()));

        return personRepository.findByIdentIn(List.of(ident, identRelasjon),
                PageRequest.of(0, 10));
    }

    public void setRelasjon(String ident, String identRelasjon, RelasjonType relasjon) {

        setRelasjoner(identRelasjon, relasjon, ident, null);
    }

    private void createRelasjon(DbPerson person1, DbPerson person2, RelasjonType relasjon) {

        if (relasjonRepository.findByPersonIdent(person1.getIdent()).stream().noneMatch(relasjon1 ->
                person1.getPerson().getIdent().equals(relasjon1.getPerson().getIdent()) &&
                        person2.getPerson().getIdent().equals(relasjon1.getRelatertPerson().getIdent()) &&
                        relasjon.equals(relasjon1.getRelasjonType()))) {

            relasjonRepository.save(DbRelasjon.builder()
                    .person(person1)
                    .relatertPerson(person2)
                    .relasjonType(relasjon)
                    .sistOppdatert(now())
                    .build());
        }
    }
}
