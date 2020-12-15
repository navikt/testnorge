package no.nav.organisasjonforvalter.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Forretningsadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.Naeringskode;
import no.nav.registre.testnorge.libs.avro.organisasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.Postadresse;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.AnsatteProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.DetaljertNavnProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EpostProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.ForretningsadresseProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.InternettadresseProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.KnytningProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.NaeringskodeProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.NavnProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OrganisasjonProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.PostadresseProducer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonMottakConsumer {

    private final AnsatteProducer ansatteProducer;
    private final DetaljertNavnProducer detaljertNavnProducer;
    private final NavnProducer navnProducer;
    private final OrganisasjonProducer organisasjonProducer;
    private final KnytningProducer knytningProducer;
    private final InternettadresseProducer internettadresseProducer;
    private final EpostProducer epostProducer;
    private final NaeringskodeProducer naeringskodeProducer;
    private final ForretningsadresseProducer forretningsadresseProducer;
    private final PostadresseProducer postadresseProducer;

    public void send(Organisasjon value) {
        String key = UUID.randomUUID().toString();
        log.info("Sender melding med navn {}, orgnr {} og key {} til Kafka", value.getNavn(), value.getMetadata().getOrgnummer(), key);
        organisasjonProducer.send(key, value);
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

    public void send(Internettadresse value) {
        internettadresseProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Epost value) {
        epostProducer.send(UUID.randomUUID().toString(), value);
    }

    public void send(Naeringskode value) {
        naeringskodeProducer.send(UUID.randomUUID().toString(), value);
    }
}
