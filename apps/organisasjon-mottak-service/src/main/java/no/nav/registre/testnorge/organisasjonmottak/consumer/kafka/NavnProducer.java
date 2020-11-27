package no.nav.registre.testnorge.organisasjonmottak.consumer.kafka;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organiasjon.Navn;
import no.nav.registre.testnorge.organisasjonmottak.config.ApplicationKafkaProperties;

@Component
public class NavnProducer extends KafkaProducer<Navn> {
    NavnProducer(ApplicationKafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Navn value) {
        getKafkaTemplate().send("tn-organiasjon-set-navn-v1", key, value);
    }
}
