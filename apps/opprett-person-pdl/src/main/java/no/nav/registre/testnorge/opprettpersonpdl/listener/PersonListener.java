package no.nav.registre.testnorge.opprettpersonpdl.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.opprettpersonpdl.consumer.PersonConsumer;
import no.nav.registre.testnorge.opprettpersonpdl.domain.Person;


@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class PersonListener {

    private final PersonConsumer personConsumer;

    @KafkaListener(topics = "testnorge-opprett-person-v1")
    public void register(@Payload no.nav.testnav.libs.avro.person.Person person) {
        log.info("Ny person registert med ident {}", person.getIdent());
        personConsumer.createPerson(new Person(person));
    }
}