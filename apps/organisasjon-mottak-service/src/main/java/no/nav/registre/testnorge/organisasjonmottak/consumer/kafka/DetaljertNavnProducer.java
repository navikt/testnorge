package no.nav.registre.testnorge.organisasjonmottak.consumer.kafka;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.organisasjonmottak.config.ApplicationKafkaProperties;

@Component
public class DetaljertNavnProducer extends KafkaProducer<DetaljertNavn> {
    DetaljertNavnProducer(ApplicationKafkaProperties properties) {
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
        getKafkaTemplate().send("tn-organisasjon-set-navn-detaljer-v1", key, value);
    }
}
