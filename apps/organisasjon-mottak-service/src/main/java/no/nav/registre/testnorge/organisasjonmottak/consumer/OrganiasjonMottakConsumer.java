package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organisasjon.organisasjon;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.AnsatteProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.DetaljertNavnProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.NavnProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.OrganisasjonProducer;

@Component
@RequiredArgsConstructor
public class organisasjonMottakConsumer {
    private final AnsatteProducer ansatteProducer;
    private final DetaljertNavnProducer detaljertNavnProducer;
    private final NavnProducer navnProducer;
    private final OrganisasjonProducer organisasjonProducer;

    public void send(organisasjon value) {
        organisasjonProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Navn value) {
        navnProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(DetaljertNavn value) {
        detaljertNavnProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Ansatte value) {
        ansatteProducer.send(UUID.randomUUID().toString(), value);
    }
}
