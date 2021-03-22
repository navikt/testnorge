package no.nav.organisasjonforvalter.config;

import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.EndringsdokumentV2Producer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OpprettelsesdokumentV2Producer.class, EndringsdokumentV2Producer.class})
public class KafkaProducerConfig {
}
