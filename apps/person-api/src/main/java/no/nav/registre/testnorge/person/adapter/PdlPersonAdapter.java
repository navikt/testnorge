package no.nav.registre.testnorge.person.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import no.nav.registre.testnorge.person.consumer.PdlApiConsumer;
import no.nav.registre.testnorge.person.consumer.PdlTestdataConsumer;
import no.nav.registre.testnorge.person.domain.Person;


@Component
@RequiredArgsConstructor
public class PdlPersonAdapter implements PersonAdapter {

    private final PdlTestdataConsumer consumer;
    private final PdlApiConsumer pdlApiConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;

    @Override
    public String createPerson(Person person, String kilde) {
        return consumer.createPerson(person, kilde);
    }

    @Override
    public Person getPerson(String ident) {
        Person person = pdlApiConsumer.getPerson(ident);

        Set<String> tags = pdlTestdataConsumer.getTags(ident);
        person.setTags(tags);
        return person;
    }

    @Override
    public Person getPerson(String ident, String miljoe) {
        return getPerson(ident);
    }
}
