package no.nav.registre.testnorge.organisasjonmottak.consumer.kafka;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn;
import no.nav.registre.testnorge.organisasjonmottak.config.ApplicationKafkaProperties;

@Component
public class DetajertNavnProducer extends KafkaProducer<DetaljertNavn> {
    DetajertNavnProducer(ApplicationKafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, DetaljertNavn value) {
        getKafkaTemplate().send("tn-organiasjon-set-navn-detaljer-v1", key, value);
    }
}
