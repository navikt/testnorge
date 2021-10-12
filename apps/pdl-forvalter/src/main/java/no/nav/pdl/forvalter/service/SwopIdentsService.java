package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private static void swopOpplysninger(DbPerson person1, DbPerson person2, boolean newNavn) {

        var ident = person1.getPerson().getIdent();
        person1.setIdent(person2.getPerson().getIdent());
        person2.setIdent(ident);
        person2.getPerson().setIdent(ident);

        if (newNavn) {
            person2.getPerson().setNavn(person1.getPerson().getNavn());
        }
        person2.getPerson().setFoedsel(person1.getPerson().getFoedsel());
        person2.getPerson().setKjoenn(person1.getPerson().getKjoenn());
        person2.getPerson().setNyident(person1.getPerson().getNyident());
    }

    public void execute(String ident1, String ident2, boolean newNavn) {

        var personer = personRepository.findByIdentIn(List.of(ident1, ident2))
                .collectList()
                .block()
                .stream()
                .collect(Collectors.toMap(DbPerson::getIdent, person -> person));

        if (personer.containsKey(ident1) && personer.containsKey(ident2)) {

            personer.get(ident1).setIdent(opaqifyIdent(ident1));
            personer.get(ident2).setIdent(opaqifyIdent(ident2));
            personRepository.saveAll(List.of(personer.get(ident1), personer.get(ident2))).collectList().block();

            var oppdatertePersoner = personRepository.findByIdIn(List.of(personer.get(ident1).getId(), personer.get(ident2).getId()))
                    .collectList()
                    .block();

            var oppdatertPerson1 = oppdatertePersoner.stream()
                    .filter(person -> personer.get(ident1).getId().equals(person.getId()))
                    .findFirst();
            var oppdatertPerson2 = oppdatertePersoner.stream()
                    .filter(person -> personer.get(ident2).getId().equals(person.getId()))
                    .findFirst();

            if (oppdatertPerson1.isPresent() && oppdatertPerson2.isPresent()) {
                swopOpplysninger(oppdatertPerson1.get(), oppdatertPerson2.get(), newNavn);

                personRepository.saveAll(List.of(personer.get(ident1), personer.get(ident2)));

                aliasRepository.save(DbAlias.builder()
                        .tidligereIdent(ident1)
                        .personId(personer.get(ident2).getId())
                        .sistOppdatert(LocalDateTime.now())
                        .build());
            }
        }
    }
}