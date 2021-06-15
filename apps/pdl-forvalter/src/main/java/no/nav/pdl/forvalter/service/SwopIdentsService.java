package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
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

    private static void swopName(DbPerson person1, DbPerson person2) {

        var navn = person1.getPerson().getNavn();
        person1.getPerson().setNavn(person2.getPerson().getNavn());
        person2.getPerson().setNavn(navn);
    }

    private static void swopFoedsel(DbPerson person1, DbPerson person2) {

        var foedsel = person1.getPerson().getFoedsel();
        person1.getPerson().setFoedsel(person2.getPerson().getFoedsel());
        person2.getPerson().setFoedsel(foedsel);
    }

    private static void swopKjoenn(DbPerson person1, DbPerson person2) {

        var kjoenn = person1.getPerson().getKjoenn();
        person1.getPerson().setKjoenn(person2.getPerson().getKjoenn());
        person2.getPerson().setKjoenn(kjoenn);
    }

    private static void swopIdent(DbPerson person1, DbPerson person2) {

        var ident = person1.getPerson().getIdent();
        person1.setIdent(person2.getPerson().getIdent());
        person1.getPerson().setIdent(person2.getPerson().getIdent());
        person2.setIdent(ident);
        person2.getPerson().setIdent(ident);
    }

    public void execute(String ident1, String ident2, boolean newNavn) {

        var personer = personRepository.findByIdentIn(List.of(ident1, ident2));
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
                    .findFirst().get();
            var oppdatertPerson2 = oppdatertePersoner.stream()
                    .filter(person -> person2.get().getId().equals(person.getId()))
                    .findFirst().get();

            swopIdent(oppdatertPerson1, oppdatertPerson2);
            swopKjoenn(oppdatertPerson1, oppdatertPerson2);
            swopFoedsel(oppdatertPerson1, oppdatertPerson2);
            if (newNavn) {
                swopName(oppdatertPerson1, oppdatertPerson2);
            }

            personRepository.saveAll(List.of(person1.get(), person2.get()));

            aliasRepository.save(DbAlias.builder()
                    .tidligereIdent(ident1)
                    .person(oppdatertPerson1)
                    .sistOppdatert(LocalDateTime.now())
                    .build());
        }
    }
}
