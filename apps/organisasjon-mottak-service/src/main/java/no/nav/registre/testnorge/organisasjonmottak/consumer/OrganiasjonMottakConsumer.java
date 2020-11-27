package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import no.nav.registre.testnorge.libs.avro.organiasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organiasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.AnsatteProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.DetajertNavnProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.NavnProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.OrganaisasjonProducer;

@Component
@RequiredArgsConstructor
public class OrganiasjonMottakConsumer {
    private final AnsatteProducer ansatteProducer;
    private final DetajertNavnProducer detajertNavnProducer;
    private final NavnProducer navnProducer;
    private final OrganaisasjonProducer organaisasjonProducer;

    public void send(Organiasjon value) {
        organaisasjonProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Navn value) {
        navnProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(DetaljertNavn value) {
        detajertNavnProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Ansatte value) {
        ansatteProducer.send(UUID.randomUUID().toString(), value);
    }
}
