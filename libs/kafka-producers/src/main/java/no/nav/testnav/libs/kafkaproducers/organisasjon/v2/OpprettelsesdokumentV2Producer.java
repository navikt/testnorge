package no.nav.testnav.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.kafkaconfig.topic.v2.OrganisasjonTopic;

@Slf4j
@Component
public class OpprettelsesdokumentV2Producer extends RecreateKafkaProducer<Opprettelsesdokument> {
    private final String groupId;

    public OpprettelsesdokumentV2Producer(@Value("${kafka.groupid}") String groupid) {
        this.groupId = groupid;
    }

    @Override
    KafkaProducer<Opprettelsesdokument> create() {
        return new KafkaProducer<>(groupId) {
            @Override
            ListenableFuture<SendResult<String, Opprettelsesdokument>> send(String key, Opprettelsesdokument value) {
                log.info("Sender opprettelsesdokument med uuid {} for organisasjon {}.", key, value.toString());
                return getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON, key, value);
            }
        };
    }
}