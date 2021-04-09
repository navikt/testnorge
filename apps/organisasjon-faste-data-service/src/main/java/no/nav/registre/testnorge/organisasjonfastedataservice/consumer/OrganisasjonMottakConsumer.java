package no.nav.registre.testnorge.organisasjonfastedataservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.EndringsdokumentV2Producer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Adresse;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;

@Component
@RequiredArgsConstructor
public class OrganisasjonMottakConsumer {
    private final OpprettelsesdokumentV2Producer opprettelsesdokumentProducer;
    private final EndringsdokumentV2Producer endringsdokumentProducer;

    public String create(Organisasjon organisasjon, String miljo, String ordreId) {
        var opprettelsesdokument = Opprettelsesdokument
                .newBuilder();

        var metadata = Metadata
                .newBuilder()
                .setMiljo(miljo)
                .build();

        opprettelsesdokumentProducer.send(ordreId, opprettelsesdokument
                .setOrganisasjon(create(organisasjon))
                .setMetadata(metadata)
                .build()
        );
        return ordreId;
    }




    public String change(Organisasjon organisasjon, String miljo, String ordreId) {
        var endringsdokumentBuilder = Endringsdokument
                .newBuilder();

        var metadata = Metadata
                .newBuilder()
                .setMiljo(miljo)
                .build();

        endringsdokumentProducer.send(ordreId, endringsdokumentBuilder
                .setOrganisasjon(create(organisasjon))
                .setMetadata(metadata)
                .build()
        );
        return ordreId;
    }

    private no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon create(Organisasjon organisasjon) {
        var builder = no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.newBuilder();

        Optional.ofNullable(organisasjon.getInternetAdresse())
                .ifPresent(value -> builder.setInternettadresseBuilder(Internettadresse.newBuilder().setInternettadresse(value)));
        Optional.ofNullable(organisasjon.getEpost())
                .ifPresent(value -> builder.setEpostBuilder(Epost.newBuilder().setEpost(value)));

        return builder
                .setOrgnummer(organisasjon.getOrgnummer())
                .setEnhetstype(organisasjon.getEnhetstype())
                .setNavnBuilder(DetaljertNavn
                        .newBuilder()
                        .setNavn1(organisasjon.getNavn())
                        .setRedigertNavn(organisasjon.getRedigertNavn())
                )
                .setUnderenheter(organisasjon.getUnderenheter().stream().map(this::create).collect(Collectors.toList()))
                .setPostadresse(create(organisasjon.getPostadresse()))
                .setForretningsadresse(create(organisasjon.getForretningsAdresse()))
                .build();
    }


    private no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse create(Adresse adresse) {
        if (adresse == null) {
            return null;
        }
        return no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse.newBuilder()
                .setPostadresse1(adresse.getAdresselinje1())
                .setPostadresse2(adresse.getAdresselinje2())
                .setPostadresse3(adresse.getAdresselinje3())
                .setKommunenummer(adresse.getKommunenr())
                .setPoststed(adresse.getPoststed())
                .setPostnummer(adresse.getPostnr())
                .setLandkode(adresse.getLandkode())
                .build();
    }
}
