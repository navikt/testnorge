package no.nav.registre.testnorge.person.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.person.consumer.TpsForvalterenConsumer;
import no.nav.registre.testnorge.person.domain.Person;

@Component
@RequiredArgsConstructor
public class TpsPersonAdapter implements PersonAdapter {
    private final TpsForvalterenConsumer tpsForvalterenConsumer;

    @Override
    public void createPerson(Person person) {
    }

    @Override
    public Person getPerson(String ident) {
        return getPerson(ident, "q1");
    }

    @Override
    public Person getPerson(String ident, String miljoe) {
        return tpsForvalterenConsumer.getPerson(ident, miljoe);
    }
}
