package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SwopIdentsService {

    private final PersonRepository personRepository;
    private final AliasRepository aliasRepository;

    private static String opaqifyIdent(String ident) {

        return new StringBuilder()
                .append('X')
                .append(ident.substring(1))
                .toString();
    }

    private static void swopOpplysninger(DbPerson person1, DbPerson person2) {

        var person = person1.getPerson();
        person1.setPerson(person2.getPerson());
        person2.setPerson(person);

        person1.setIdent(person1.getPerson().getIdent());
        person2.setIdent(person2.getPerson().getIdent());

        var navn = person2.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO());
        person2.setFornavn(navn.getFornavn());
        person2.setMellomnavn(navn.getMellomnavn());
        person2.setEtternavn(navn.getEtternavn());
    }

    public void execute(String ident1, String ident2) {

        var personer = personRepository.findByIdentIn(List.of(ident1, ident2),
                PageRequest.of(0, 10));
        var person1 = personer.stream()
                .filter(person -> ident1.equals(person.getIdent()))
                .findFirst();
        var person2 = personer.stream()
                .filter(person -> ident2.equals(person.getIdent()))
                .findFirst();

        if (person1.isPresent() && person2.isPresent()) {

            person1.get().setIdent(opaqifyIdent(ident1));
            person2.get().setIdent(opaqifyIdent(ident2));
            personRepository.saveAll(List.of(person1.get(), person2.get()));

            var oppdatertePersoner = personRepository.findByIdIn(List.of(person1.get().getId(), person2.get().getId()));
            var oppdatertPerson1 = oppdatertePersoner.stream()
                    .filter(person -> person1.get().getId().equals(person.getId()))
                    .findFirst();
            var oppdatertPerson2 = oppdatertePersoner.stream()
                    .filter(person -> person2.get().getId().equals(person.getId()))
                    .findFirst();

            if (oppdatertPerson1.isPresent() && oppdatertPerson2.isPresent()) {
                swopOpplysninger(oppdatertPerson1.get(), oppdatertPerson2.get());

                personRepository.saveAll(List.of(person1.get(), person2.get()));

                aliasRepository.save(DbAlias.builder()
                        .tidligereIdent(ident1)
                        .person(oppdatertPerson1.get())
                        .sistOppdatert(LocalDateTime.now())
                        .build());
            }
        }
    }
}