package no.nav.registre.testnorge.organisasjonmottak.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.AnsatteProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.DetaljertNavnProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EpostProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.ForretningsadresseProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.InternettadresseProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.KnytningProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.NaeringskodeProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.NavnProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OrganisasjonProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.PostadresseProducer;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        SecureOAuth2ServerToServerConfiguration.class,
        ApplicationCoreConfig.class,
        KafkaProperties.class,
        AnsatteProducer.class,
        DetaljertNavnProducer.class,
        EpostProducer.class,
        ForretningsadresseProducer.class,
        InternettadresseProducer.class,
        KnytningProducer.class,
        NaeringskodeProducer.class,
        OrganisasjonProducer.class,
        PostadresseProducer.class,
        NavnProducer.class
})
@EnableScheduling
public class ApplicationConfig {
}