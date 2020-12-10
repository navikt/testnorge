package no.nav.registre.sdforvalter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.kafkaconfig.config.KafkaProperties;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EpostProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.ForretningsadresseProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.InternettadresseProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.KnytningProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.NaeringskodeProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.NavnProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OrganisasjonProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.PostadresseProducer;

@Import({
        KafkaProperties.class,
        EpostProducer.class,
        ForretningsadresseProducer.class,
        InternettadresseProducer.class,
        KnytningProducer.class,
        NaeringskodeProducer.class,
        OrganisasjonProducer.class,
        PostadresseProducer.class,
        NavnProducer.class
})
@Configuration
public class KafkaConfig {
}
