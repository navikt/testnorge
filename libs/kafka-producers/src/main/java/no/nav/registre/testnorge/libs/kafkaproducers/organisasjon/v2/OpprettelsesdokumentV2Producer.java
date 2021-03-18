package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.v2.OrganisasjonTopic;

@Slf4j
@Component
public class OpprettelsesdokumentV2Producer extends RecreateKafkaProducer<Opprettelsesdokument> {
    private final String groupid;

    public OpprettelsesdokumentV2Producer(@Value("${kafka.groupid}") String groupid) {
        this.groupid = groupid;
    }

    @Override
    KafkaProducer<Opprettelsesdokument> create() {
        return new KafkaProducer<>(groupid) {
            @Override
            ListenableFuture<SendResult<String, Opprettelsesdokument>> send(String key, Opprettelsesdokument value) {
                return getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON, key, value);
            }
        };
    }
}