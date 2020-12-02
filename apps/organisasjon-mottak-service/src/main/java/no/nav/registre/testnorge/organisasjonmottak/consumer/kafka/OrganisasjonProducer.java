package no.nav.registre.testnorge.organisasjonmottak.consumer.kafka;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottak.config.ApplicationKafkaProperties;

@Component
public class OrganisasjonProducer extends KafkaProducer<Organiasjon> {
    OrganisasjonProducer(ApplicationKafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Organiasjon value) {
        getKafkaTemplate().send("tn-opprett-organisasjon-v1", key, value);
    }
}
