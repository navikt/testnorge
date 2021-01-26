package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.analysis.NaisDependencyOn;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.OrganisasjonTopic;
import no.nav.registre.testnorge.libs.kafkaproducers.KafkaProducer;

@Slf4j
@Component
@NaisDependencyOn(
        name = "organisasjon-orgnummer-service",
        cluster = "dev-fss",
        namespace = "dolly"
)
public class EndringsdokumentProducer extends KafkaProducer<Endringsdokument> {
    EndringsdokumentProducer(KafkaProperties properties) {
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