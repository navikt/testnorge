package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SwopIdentsService {

    private final PersonRepository personRepository;

    public void execute(String ident1, String ident2) {

        var personer =personRepository.findByIdentIn(List.of(ident1, ident2));
        var person1 = personer.stream().findFirst();
        var person2 = personer.stream().reduce((first, last) -> last);

        if (person1.isPresent() && person2.isPresent()) {
            var temp = person1.get().getIdent();
            person1.get().setIdent(person2.get().getIdent());
            person2.get().setIdent(temp);
            personRepository.saveAll(List.of(person1.get(), person2.get()));
        }
    }
}
