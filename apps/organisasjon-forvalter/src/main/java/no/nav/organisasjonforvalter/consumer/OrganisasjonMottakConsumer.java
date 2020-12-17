package no.nav.organisasjonforvalter.consumer;

import javassist.bytecode.Descriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.*;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.*;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

    public void sendOrgnavn(String key, Organisasjon organisasjon, String env) {
        organisasjonProducer.send(key, no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon.newBuilder()
                .setNavn(organisasjon.getOrganisasjonsnavn())
                .setMetadata(getMetadata(organisasjon, env))
                .build());
    }

    public void sendNaeringskode(String key, Organisasjon organisasjon, String env) {

        if (isNotBlank(organisasjon.getNaeringskode())) {
            naeringskodeProducer.send(key, Naeringskode.newBuilder()
                    .setKode(organisasjon.getNaeringskode())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendInternetadresse(String key, Organisasjon organisasjon, String env) {

        if (isNotBlank(organisasjon.getNettside())) {
            internettadresseProducer.send(key, Internettadresse.newBuilder()
                    .setInternettadresse(organisasjon.getNettside())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendEpost(String key, Organisasjon organisasjon, String env) {

        if (isNotBlank(organisasjon.getEpost())) {
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
            String[]  adresselinjer = adresse.get().getAdresse().split(",");
            forretningsadresseProducer.send(key, Forretningsadresse.newBuilder()
                    .setPostadresse1(adresselinjer.length > 0 ? adresselinjer[0] : null)
                    .setPostadresse2(adresselinjer.length > 1 ? adresselinjer[1] : null)
                    .setPostadresse3(adresselinjer.length > 2 ? adresselinjer[2] : null)
                    .setKommunenummer(adresse.get().getKommunenr())
                    .setPostnummer(adresse.get().getPostnr())
                    .setPoststed(adresse.get().getPoststed())
                    .setLandkode(adresse.get().getLandkode())
                    .setVegadresseId(adresse.get().getVegadresseId())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendPostadresse(String key, Organisasjon organisasjon, String env) {
        Optional<Adresse> adresse = organisasjon.getAdresser().stream().
                filter(Adresse::isPostadresse).findFirst();

        if (adresse.isPresent()) {
            String[]  adresselinjer = adresse.get().getAdresse().split(",");
            postadresseProducer.send(key, Postadresse.newBuilder()
                    .setPostadresse1(adresselinjer.length > 0 ? adresselinjer[0] : null)
                    .setPostadresse2(adresselinjer.length > 1 ? adresselinjer[1] : null)
                    .setPostadresse3(adresselinjer.length > 2 ? adresselinjer[2] : null)
                    .setKommunenummer(adresse.get().getKommunenr())
                    .setPostnummer(adresse.get().getPostnr())
                    .setPoststed(adresse.get().getPoststed())
                    .setLandkode(adresse.get().getLandkode())
                    .setVegadresseId(adresse.get().getVegadresseId())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    public void sendParent(String key, Organisasjon organisasjon, String env) {

        if (nonNull(organisasjon.getParent())) {
            knytningProducer.send(key, Knytning.newBuilder()
                    .setJuridiskEnhet(organisasjon.getParent().getOrganisasjonsnummer())
                    .setMetadata(getMetadata(organisasjon, env))
                    .build());
        }
    }

    private static Metadata getMetadata(Organisasjon organisasjon, String miljloe) {
        return Metadata.newBuilder()
                .setOrgnummer(organisasjon.getOrganisasjonsnummer())
                .setEnhetstype(organisasjon.getEnhetstype())
                .setMiljo(miljloe)
                .build();
    }
}
