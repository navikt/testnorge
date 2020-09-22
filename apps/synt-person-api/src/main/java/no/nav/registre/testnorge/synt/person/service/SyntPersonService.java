package no.nav.registre.testnorge.synt.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.synt.person.consumer.IdentPoolConsumer;
import no.nav.registre.testnorge.synt.person.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;
import no.nav.registre.testnorge.synt.person.domain.Person;

@Service
@RequiredArgsConstructor
public class SyntPersonService {
    private final IdentPoolConsumer identPoolConsumer;
    private final SyntrestConsumer syntrestConsumer;

    public Person createSyntPerson() {
        String ident = identPoolConsumer.getIdent();
        SyntPersonDTO syntPerson = syntrestConsumer.createSyntPerson();
        return new Person(syntPerson, ident);
    }
}