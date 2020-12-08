package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1;

import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.OrganisasjonTopic;
import no.nav.registre.testnorge.libs.kafkaproducers.KafkaProducer;

@Component
public class InternettadresseProducer extends KafkaProducer<Internettadresse> {
    InternettadresseProducer(KafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Internettadresse value) {
        getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_SET_INTERNETTADRESSE, key, value);
    }
}
