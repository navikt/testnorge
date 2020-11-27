package no.nav.registre.testnorge.organisasjonmottak.consumer.kafka;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organiasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organiasjon.Navn;
import no.nav.registre.testnorge.organisasjonmottak.config.ApplicationKafkaProperties;

@Component
public class AnsatteProducer extends KafkaProducer<Ansatte> {
    AnsatteProducer(ApplicationKafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Ansatte value) {
        getKafkaTemplate().send("tn-organiasjon-set-ansatte-v1", key, value);
    }
}
