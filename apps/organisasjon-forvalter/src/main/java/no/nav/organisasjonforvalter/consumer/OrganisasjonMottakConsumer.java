package no.nav.organisasjonforvalter.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EndringsdokumentProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentProducer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonMottakConsumer {

    private final OrganisasjonProducer organisasjonProducer;
    private final KnytningProducer knytningProducer;
    private final InternettadresseProducer internettadresseProducer;
    private final EpostProducer epostProducer;
    private final NaeringskodeProducer naeringskodeProducer;
    private final ForretningsadresseProducer forretningsadresseProducer;
    private final PostadresseProducer postadresseProducer;

    private static Metadata getMetadata(Organisasjon organisasjon, String miljloe) {
        return Metadata.newBuilder()
                .setOrgnummer(organisasjon.getOrganisasjonsnummer())
                .setEnhetstype(organisasjon.getEnhetstype())
                .setMiljo(miljloe)
                .build();
    }

    private static Dato getDate(LocalDate date) {
        return Dato.newBuilder()
                .setAar(date.getYear())
                .setMaaned(date.getMonthValue())
                .setDag(date.getDayOfMonth())
                .build();
    }

    //TODO Fjerne så snart producer er oppdatert
    private static String fixPoststed(String poststed) {
        return isNotBlank(poststed) ? poststed : "Verdi for poststed";
    }

    //TODO Hva er dette?
    private static String fixLinjenr(String vegadresseId) {
        return isNotBlank(vegadresseId) ? "1" : null;
    }

    private static Metadata getMetadata(String miljoe) {
        return Metadata.newBuilder()
                .setMiljo(miljoe)
                .build();
    }

    public void opprettOrganisasjon(String key, Organisasjon organisasjon, String miljoe) {

        log.info("Sender opprettOrganisasjon med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), miljoe);
        opprettelsesdokumentProducer.send(key, Opprettelsesdokument.newBuilder()
                .setOrganisasjon(mapperFacade.map(organisasjon, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class))
                .setMetadata(getMetadata(miljoe))
                .build());
    }

    public void endreOrganisasjon(String key, Organisasjon organisasjon, String miljoe) {

        log.info("Sender endreOrganisasjon med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), miljoe);
        endringsdokumentProducer.send(key, Endringsdokument.newBuilder()
                .setOrganisasjon(mapperFacade.map(organisasjon, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class))
                .setMetadata(getMetadata(miljoe))
                .build());
    }
}
