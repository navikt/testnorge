package no.nav.registre.testnorge.mn.personservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import no.nav.registre.testnorge.mn.personservice.consumer.PersonApiConsumer;
import no.nav.registre.testnorge.mn.personservice.domain.Person;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final IdentService identService;
    private final PersonApiConsumer personApiConsumer;

    public List<Person> getMiniNorgePersoner() {
        Set<String> identer = identService.getIdenter();
        return personApiConsumer.getPersoner(identer);
    }

}
