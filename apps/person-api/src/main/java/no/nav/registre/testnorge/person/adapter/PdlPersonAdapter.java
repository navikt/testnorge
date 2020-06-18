package no.nav.registre.testnorge.person.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.person.consumer.PdlTestdataConsumer;
import no.nav.registre.testnorge.person.domain.Person;


@Component
@RequiredArgsConstructor
public class PdlPersonAdapter implements PersonAdapter {

    private final PdlTestdataConsumer consumer;

    @Override
    public void createPerson(Person person) {
        consumer.createPerson(person);
    }
}
