package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Forretningsadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.Postadresse;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.AnsatteProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.DetaljertNavnProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.ForretningsadresseProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.KnytningProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.NavnProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.OrganisasjonProducer;
import no.nav.registre.testnorge.organisasjonmottak.consumer.kafka.PostadresseProducer;

@Component
@RequiredArgsConstructor
public class OrganisasjonMottakConsumer {
    private final AnsatteProducer ansatteProducer;
    private final DetaljertNavnProducer detaljertNavnProducer;
    private final NavnProducer navnProducer;
    private final OrganisasjonProducer organisasjonProducer;
    private final KnytningProducer knytningProducer;
    private final ForretningsadresseProducer forretningsadresseProducer;
    private final PostadresseProducer postadresseProducer;

    public void send(Organisasjon value) {
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

    public void send(Knytning value) {
        knytningProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Postadresse value) {
        postadresseProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Forretningsadresse value) {
        forretningsadresseProducer.send(UUID.randomUUID().toString(), value);
    }
}
