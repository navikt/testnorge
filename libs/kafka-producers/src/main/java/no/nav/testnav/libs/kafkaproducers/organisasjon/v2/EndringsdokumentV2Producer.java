package no.nav.testnav.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import no.nav.testnav.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.testnav.libs.kafkaconfig.topic.v2.OrganisasjonTopic;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class EndringsdokumentV2Producer extends RecreateKafkaProducer<Endringsdokument> {
    private final String groupId;

    EndringsdokumentV2Producer(@Value("${kafka.groupid}") String groupId) {
        this.groupId = groupId;
    }

    @Override
    KafkaProducer<Endringsdokument> create() {
        return new KafkaProducer<>(groupId) {
            @Override
            CompletableFuture<SendResult<String, Endringsdokument>> send(String key, Endringsdokument value) {
                log.info("Sender endringsdokument med uuid {} for organisasjon {}.", key, value.getOrganisasjon().getOrgnummer());
                return getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON, key, value);
            }
        };
    }
}