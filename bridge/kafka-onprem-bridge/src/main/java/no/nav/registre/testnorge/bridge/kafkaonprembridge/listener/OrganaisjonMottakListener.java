package no.nav.registre.testnorge.bridge.kafkaonprembridge.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.v1.OrganisasjonTopic;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.EndringsdokumentV2Producer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganaisjonMottakListener {

    private final EndringsdokumentV2Producer endringsdokumentProducer;
    private final OpprettelsesdokumentV2Producer opprettelsesdokumentProducer;

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON)
    public void create(ConsumerRecord<String, Opprettelsesdokument> record) {
        log.info("Bridger {} with uuid {}.", OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON, record.key());
        opprettelsesdokumentProducer.send(record.key(), record.value());
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON)
    public void update(ConsumerRecord<String, Endringsdokument> record) {
        log.info("Bridger {} with uuid {}.", OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON, record.key());
        endringsdokumentProducer.send(record.key(), record.value());
    }

}
