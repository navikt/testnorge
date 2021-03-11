package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.OrganisasjonTopic;

@Slf4j
@Component
public class EndringsdokumentV1Producer extends KafkaProducer<Endringsdokument> {
    EndringsdokumentV1Producer(KafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Endringsdokument value) {
        log.info("Sender endringsdokument {} for organisasjon {}.", key, value.getOrganisasjon().getOrgnummer());
        getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON, key, value);
    }
}