package no.nav.registre.testnorge.organisasjonmottak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.KafkaProducer;

@Component
public class SendMetadata extends KafkaProducer<Metadata> {
    public SendMetadata(@Value("${kafka.groupid}") String groupId) {
        super(groupId);
    }

    @Override
    public void send(String key, Metadata value) {
        super.getKafkaTemplate().send("dolly.testnav-opprett-organisasjon-1", value);
    }
}
