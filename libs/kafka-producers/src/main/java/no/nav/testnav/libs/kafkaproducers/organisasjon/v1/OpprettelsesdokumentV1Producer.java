package no.nav.testnav.libs.kafkaproducers.organisasjon.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.kafkaconfig.config.KafkaProperties;
import no.nav.testnav.libs.kafkaconfig.topic.v1.OrganisasjonTopic;

@Slf4j
@Component
public class OpprettelsesdokumentV1Producer extends KafkaProducer<Opprettelsesdokument> {
    OpprettelsesdokumentV1Producer(KafkaProperties properties) {
        super(
                properties.getBootstrapAddress(),
                properties.getGroupId(),
                properties.getSchemaregistryServers(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Override
    public void send(String key, Opprettelsesdokument value) {
        log.info("Sender opprettelsesdokument {} for organisasjon {}.", key, value.getOrganisasjon().getOrgnummer());
        getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON, key, value);
    }
}