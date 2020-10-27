package no.nav.registre.testnorge.synt.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.testnorge.synt.person.consumer.IdentPoolConsumer;
import no.nav.registre.testnorge.synt.person.consumer.PersonApiConsumer;
import no.nav.registre.testnorge.synt.person.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;
import no.nav.registre.testnorge.synt.person.domain.Person;

@Service
@RequiredArgsConstructor
public class SyntPersonService {
    private final IdentPoolConsumer identPoolConsumer;
    private final PersonApiConsumer personApiConsumer;
    private final SyntrestConsumer syntrestConsumer;

    public void createSyntPerson() {
        String ident = identPoolConsumer.getIdent();
        SyntPersonDTO syntPerson = getSyntPerson("1").get(0);
        personApiConsumer.createPerson(new Person(syntPerson, ident));
    }

    public List<SyntPersonDTO> getSyntPerson(String antall) {
        return syntrestConsumer.createSyntPerson(antall);
    }
}