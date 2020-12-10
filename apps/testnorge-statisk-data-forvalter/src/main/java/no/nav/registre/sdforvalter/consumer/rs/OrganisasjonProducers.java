package no.nav.registre.sdforvalter.consumer.rs;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.Kode;
import no.nav.registre.sdforvalter.domain.Kodeverk;
import no.nav.registre.testnorge.libs.avro.organisasjon.Dato;
import no.nav.registre.testnorge.libs.avro.organisasjon.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.Forretningsadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.Naeringskode;
import no.nav.registre.testnorge.libs.avro.organisasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.Postadresse;
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

@Component
@RequiredArgsConstructor
class OrganisasjonProducers {

    private final GenerereNavnConsumer generereNavnConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final NavnProducer navnProducer;
    private final OrganisasjonProducer organisasjonProducer;
    private final KnytningProducer knytningProducer;
    private final InternettadresseProducer internettadresseProducer;
    private final EpostProducer epostProducer;
    private final NaeringskodeProducer naeringskodeProducer;
    private final ForretningsadresseProducer forretningsadresseProducer;
    private final PostadresseProducer postadresseProducer;

    private Metadata getMetadata(Ereg ereg, String miljo) {
        return Metadata
                .newBuilder()
                .setEnhetstype(ereg.getEnhetstype())
                .setOrgnummer(ereg.getOrgnr())
                .setMiljo(miljo)
                .build();
    }

    private Kode getNaeringskode(Ereg ereg) {
        Kodeverk kodeverk = kodeverkConsumer.getKodeverk();
        if (ereg.getNaeringskode() != null) {
            return kodeverk
                    .getKodeByName(ereg.getNaeringskode())
                    .orElseThrow(() -> new NotImplementedException("Finner ikke kode " + ereg.getNaeringskode() + " i kodeverk."));
        }
        return kodeverk.randomKode();
    }

    private String getNavn(Ereg ereg) {
        return ereg.getNavn() != null
                ? ereg.getNavn()
                : generereNavnConsumer.genererNavn() + (ereg.getEnhetstype().equals("AS") ? " AS" : "");
    }

    private Dato toDato(LocalDate date) {
        return Dato
                .newBuilder()
                .setDag(date.getDayOfMonth())
                .setMaaned(date.getMonthValue())
                .setAar(date.getYear())
                .build();
    }

    void opprettOrganiasjon(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        var organisasjon = Organisasjon
                .newBuilder()
                .setMetadata(metadata)
                .setNavn(getNavn(ereg))
                .build();
        organisasjonProducer.send(ordreId.toString(), organisasjon);
    }

    void setNearingskode(UUID ordreId, Ereg ereg, String miljo) {
        Kode kode = getNaeringskode(ereg);
        Metadata metadata = getMetadata(ereg, miljo);
        var naeringskode = Naeringskode
                .newBuilder()
                .setMetadata(metadata)
                .setGyldighetsdato(toDato(kode.getGyldigFra()))
                .setKode(kode.getNavn())
                .setHjelpeenhet(false)
                .build();
        naeringskodeProducer.send(ordreId.toString(), naeringskode);
    }

    void setNavn(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        navnProducer.send(ordreId.toString(), Navn
                .newBuilder()
                .setMetadata(metadata)
                .setNavn(getNavn(ereg))
                .build()
        );
    }

    void setPostadresse(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        postadresseProducer.send(ordreId.toString(), Postadresse
                .newBuilder()
                .setMetadata(metadata)
                .setKommunenummer(ereg.getPostadresse().getKommunenr())
                .setPostadresse1(ereg.getPostadresse().getAdresse())
                .setPostnummer(ereg.getPostadresse().getPostnr())
                .setLandkode(ereg.getPostadresse().getLandkode())
                .setPoststed(ereg.getPostadresse().getPoststed())
                .build()
        );
    }

    void setForretningsadresse(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        forretningsadresseProducer.send(ordreId.toString(), Forretningsadresse
                .newBuilder()
                .setMetadata(metadata)
                .setKommunenummer(ereg.getForretningsAdresse().getKommunenr())
                .setPostadresse1(ereg.getForretningsAdresse().getAdresse())
                .setPostnummer(ereg.getForretningsAdresse().getPostnr())
                .setLandkode(ereg.getForretningsAdresse().getLandkode())
                .setPoststed(ereg.getForretningsAdresse().getPoststed())
                .build()
        );
    }

    void setEpost(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        epostProducer.send(ordreId.toString(), Epost
                .newBuilder()
                .setMetadata(metadata)
                .setEpost(ereg.getEpost())
                .build()
        );
    }

    void setInternetAdresse(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        internettadresseProducer.send(ordreId.toString(), Internettadresse
                .newBuilder()
                .setMetadata(metadata)
                .setInternettadresse(ereg.getInternetAdresse())
                .build()
        );
    }

    void setJuridiskEnhet(UUID ordreId, Ereg ereg, String miljo) {
        Metadata metadata = getMetadata(ereg, miljo);
        knytningProducer.send(ordreId.toString(), Knytning
                .newBuilder()
                .setMetadata(metadata)
                .setJuridiskEnhet(ereg.getJuridiskEnhet())
                .build()
        );
    }


}
