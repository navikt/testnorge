package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.Naeringskode;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.OrganisasjonTopic;
import no.nav.registre.testnorge.libs.kafkaproducers.KafkaProducer;

@Component
public class NaeringskodeProducer extends KafkaProducer<Naeringskode> {
    NaeringskodeProducer(KafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Naeringskode value) {
        getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_SET_NAERINGSKODE, key, value);
    }
}