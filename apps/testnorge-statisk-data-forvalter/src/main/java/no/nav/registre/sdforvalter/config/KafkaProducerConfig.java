package no.nav.registre.sdforvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EndringsdokumentV1Producer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentV1Producer;

@Configuration
@Import({OpprettelsesdokumentV1Producer.class, EndringsdokumentV1Producer.class})
public class KafkaProducerConfig {
}
