package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.v2.OrganisasjonTopic;

@Slf4j
@Component
public class OpprettelsesdokumentV2Producer extends KafkaProducer<Opprettelsesdokument> {
    OpprettelsesdokumentV2Producer(@Value("${kafka.groupid}") String groupid) {
        super(groupid);
    }

    @Override
    public void send(String key, Opprettelsesdokument value) {
        log.info("Sender opprettelsesdokument {} for organisasjon {}.", key, value.getOrganisasjon().getOrgnummer());
        super.getKafkaTemplate().send(OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON, key, value);
    }
}