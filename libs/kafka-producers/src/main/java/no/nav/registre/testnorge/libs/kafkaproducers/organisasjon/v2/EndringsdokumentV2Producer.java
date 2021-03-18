package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.v2.OrganisasjonTopic;

@Slf4j
@Component
public class EndringsdokumentV2Producer extends RecreateKafkaProducer<Endringsdokument> {
    private final String groupid;

    EndringsdokumentV2Producer(@Value("${kafka.groupid}") String groupid) {
        this.groupid = groupid;
    }

    @Override
    KafkaProducer<Endringsdokument> create() {
        return new KafkaProducer<>(groupid) {
            @Override
            ListenableFuture<SendResult<String, Endringsdokument>> send(String key, Endringsdokument value) {
                log.info("Sender endringsdokument {} for organisasjon {}.", key, value.getOrganisasjon().getOrgnummer());
                return getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON, key, value);
            }
        };
    }
}