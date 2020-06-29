package no.nav.registre.testnorge.person.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.person.consumer.PdlApiConsumer;
import no.nav.registre.testnorge.person.consumer.PdlTestdataConsumer;
import no.nav.registre.testnorge.person.domain.Person;


@Component
@RequiredArgsConstructor
public class PdlPersonAdapter implements PersonAdapter {

    private final PdlTestdataConsumer consumer;
    private final PdlApiConsumer pdlApiConsumer;

    @Override
    public void createPerson(Person person) {
        consumer.createPerson(person);
    }

    @Override
    public Person getPerson(String ident) {
        return pdlApiConsumer.getPerson(ident);
    }
}
