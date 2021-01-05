package no.nav.organisasjonforvalter.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.*;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

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
        return nonNull(vegadresseId) ? "1" : null;
    }

    public void sendOrgnavn(String key, Organisasjon organisasjon, String env) {

        log.info("Sender Organisasjon med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
        organisasjonProducer.send(key, no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon.newBuilder()
                .setNavn(organisasjon.getOrganisasjonsnavn())
                .setMetadata(getMetadata(organisasjon, env))
                .build());
    }

    public void sendNaeringskode(String key, Organisasjon organisasjon, String env) {

        if (isNotBlank(organisasjon.getNaeringskode())) {
            log.info("Sender Naeringskode med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
            naeringskodeProducer.send(key, Naeringskode.newBuilder()
                    .setKode(organisasjon.getNaeringskode())
                    .setGyldighetsdato(getDate(LocalDate.now()))
                    .setHjelpeenhet(false)
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendInternetadresse(String key, Organisasjon organisasjon, String env) {

        if (isNotBlank(organisasjon.getNettside())) {
            log.info("Sender Internetadresse med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
            internettadresseProducer.send(key, Internettadresse.newBuilder()
                    .setInternettadresse(organisasjon.getNettside())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendEpost(String key, Organisasjon organisasjon, String env) {

        if (isNotBlank(organisasjon.getEpost())) {
            log.info("Sender Epost med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
            epostProducer.send(key, Epost.newBuilder()
                    .setEpost(organisasjon.getEpost())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendForretningsadresse(String key, Organisasjon organisasjon, String env) {

        Optional<Adresse> adresse = organisasjon.getAdresser().stream().
                filter(Adresse::isForretningsadresse).findFirst();

        if (adresse.isPresent()) {
            log.info("Sender Forretningsadresse med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
            String[] adresselinjer = adresse.get().getAdresse().split(",");
            forretningsadresseProducer.send(key, Forretningsadresse.newBuilder()
                    .setPostadresse1(adresselinjer.length > 0 ? adresselinjer[0] : null)
                    .setPostadresse2(adresselinjer.length > 1 ? adresselinjer[1] : null)
                    .setPostadresse3(adresselinjer.length > 2 ? adresselinjer[2] : null)
                    .setKommunenummer(adresse.get().getKommunenr())
                    .setPostnummer(adresse.get().getPostnr())
                    .setPoststed(fixPoststed(adresse.get().getPoststed()))
                    .setLandkode(adresse.get().getLandkode())
                    .setLinjenummer(fixLinjenr(adresse.get().getVegadresseId()))
                    .setVegadresseId(adresse.get().getVegadresseId())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendPostadresse(String key, Organisasjon organisasjon, String env) {
        Optional<Adresse> adresse = organisasjon.getAdresser().stream().
                filter(Adresse::isPostadresse).findFirst();

        if (adresse.isPresent()) {
            log.info("Sender Postadresse med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
            String[] adresselinjer = adresse.get().getAdresse().split(",");
            postadresseProducer.send(key, Postadresse.newBuilder()
                    .setPostadresse1(adresselinjer.length > 0 ? adresselinjer[0] : null)
                    .setPostadresse2(adresselinjer.length > 1 ? adresselinjer[1] : null)
                    .setPostadresse3(adresselinjer.length > 2 ? adresselinjer[2] : null)
                    .setKommunenummer(adresse.get().getKommunenr())
                    .setPostnummer(adresse.get().getPostnr())
                    .setPoststed(fixPoststed(adresse.get().getPoststed()))
                    .setLandkode(adresse.get().getLandkode())
                    .setLinjenummer(fixLinjenr(adresse.get().getVegadresseId()))
                    .setVegadresseId(adresse.get().getVegadresseId())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendParent(String key, Organisasjon organisasjon, String env) {

        if (nonNull(organisasjon.getParent())) {
            log.info("Sender Knytning med UUID {} for {} til Kafa, env {}", key, organisasjon.getOrganisasjonsnummer(), env);
            knytningProducer.send(key, Knytning.newBuilder()
                    .setJuridiskEnhet(organisasjon.getParent().getOrganisasjonsnummer())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }
}
