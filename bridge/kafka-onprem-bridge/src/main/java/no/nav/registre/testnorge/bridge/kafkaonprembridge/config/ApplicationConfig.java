package no.nav.registre.testnorge.bridge.kafkaonprembridge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.EndringsdokumentV2Producer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;
import no.nav.registre.testnorge.libs.oauth2.config.InsecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        InsecureOAuth2ServerToServerConfiguration.class,
        KafkaProperties.class,
        OpprettelsesdokumentV2Producer.class,
        EndringsdokumentV2Producer.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {
}