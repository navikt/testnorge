package no.nav.registre.sdforvalter.consumer.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EndringsdokumentProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentProducer;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrganisasjonMottakConsumer {
    private final OpprettelsesdokumentProducer opprettelsesdokumentProducer;
    private final EndringsdokumentProducer endringsdokumentProducer;
    private final MapperFacade mapperFacade;

    private static Metadata getMetadata(String miljoe) {
        return Metadata.newBuilder()
                .setMiljo(miljoe)
                .build();
    }

    public void opprettOrganisasjon(String key, Ereg organisasjon, String miljoe) {

        log.info("Sender opprettOrganisasjon med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrgnr(), miljoe);
        opprettelsesdokumentProducer.send(key, Opprettelsesdokument.newBuilder()
                .setOrganisasjon(mapperFacade.map(organisasjon, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class))
                .setMetadata(getMetadata(miljoe))
                .build());
    }

    public void endreOrganisasjon(String key, Ereg organisasjon, String miljoe) {

        log.info("Sender endreOrganisasjon med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrgnr(), miljoe);
        endringsdokumentProducer.send(key, Endringsdokument.newBuilder()
                .setOrganisasjon(mapperFacade.map(organisasjon, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class))
                .setMetadata(getMetadata(miljoe))
                .build());
    }
}
