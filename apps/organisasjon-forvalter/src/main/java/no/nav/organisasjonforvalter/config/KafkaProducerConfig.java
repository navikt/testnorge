package no.nav.organisasjonforvalter.config;

import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KafkaProperties.class)
@ComponentScan(basePackages = "no.nav.registre.testnorge.libs.kafkaproducers")
public class KafkaProducerConfig {
}
