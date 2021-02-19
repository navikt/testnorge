package no.nav.registre.sdforvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EndringsdokumentProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentProducer;

@Configuration
@Import({KafkaProperties.class, OpprettelsesdokumentProducer.class, EndringsdokumentProducer.class})
public class KafkaProducerConfig {
}
