package no.nav.organisasjonforvalter.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.testnav.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Metadata;
import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.kafkaproducers.organisasjon.v2.EndringsdokumentV2Producer;
import no.nav.testnav.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonMottakConsumer {

    private final OpprettelsesdokumentV2Producer opprettelsesdokumentProducer;
    private final EndringsdokumentV2Producer endringsdokumentProducer;
    private final MapperFacade mapperFacade;

    public void opprettOrganisasjon(String key, Organisasjon organisasjon, String miljoe) {

        log.info("Sender opprettOrganisasjon med UUID {} for {} til Kafka, env {}", key, organisasjon.getOrganisasjonsnummer(), miljoe);
        var org = mapperFacade.map(organisasjon, no.nav.testnav.libs.avro.organisasjon.v1.Organisasjon.class);
        opprettelsesdokumentProducer.send(key, Opprettelsesdokument.newBuilder()
                .setOrganisasjon(org)
                .setMetadata(getMetadata(miljoe))
                .build());
    }

    public void endreOrganisasjon(String key, Organisasjon organisasjon, String miljoe) {

        log.info("Sender endreOrganisasjon med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), miljoe);
        endringsdokumentProducer.send(key, Endringsdokument.newBuilder()
                .setOrganisasjon(mapperFacade.map(organisasjon, no.nav.testnav.libs.avro.organisasjon.v1.Organisasjon.class))
                .setMetadata(getMetadata(miljoe))
                .build());
    }

    private static Metadata getMetadata(String miljoe) {
        return Metadata.newBuilder()
                .setMiljo(miljoe)
                .build();
    }
}
