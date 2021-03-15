package no.nav.registre.testnorge.bridge.kafkaonprembridge.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.bridge.kafkaonprembridge.consumer.KafkaCloudBridgeConsumer;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.v1.OrganisasjonTopic;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganaisjonMottakListener {

    private final KafkaCloudBridgeConsumer bridgeConsumer;

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON)
    public void create(ConsumerRecord<String, Opprettelsesdokument> record) {
        log.info("Bridge {} with uuid {}.", OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON, record.key());
        bridgeConsumer.bridge(record.key(), record.value());
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON)
    public void update(ConsumerRecord<String, Endringsdokument> record) {
        log.info("Bridge {} with uuid {}.", OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON, record.key());
        bridgeConsumer.bridge(record.key(), record.value());
    }
}
