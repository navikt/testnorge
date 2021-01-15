package no.nav.registre.testnorge.organisasjonmottak.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EndringsdokumentProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentProducer;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        SecureOAuth2ServerToServerConfiguration.class,
        ApplicationCoreConfig.class,
        KafkaProperties.class,
        OpprettelsesdokumentProducer.class,
        EndringsdokumentProducer.class
})
@EnableScheduling
public class ApplicationConfig {
}