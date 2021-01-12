package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrganisasjonDTO {
    String orgnummer;
    String enhtestype;
    NavnDTO navn;
    AdresseDTO forretningsadresse;

    public Opprettelsesdokument toOrganisasjonOpprettelsesdokument(String miljo) {
        return Opprettelsesdokument
                .newBuilder()
                .setOrganisasjonBuilder(Organisasjon
                        .newBuilder()
                        .setOrgnummer(orgnummer)
                        .setEnhetstype(enhtestype)
                        .setNavnBuilder(DetaljertNavn.newBuilder().setNavn1(navn.getNavn1()))
                        .setPostadresseBuilder(Adresse.newBuilder()
                                .setPostadresse1(forretningsadresse.getPostadresse1())
                                .setKommunenummer(forretningsadresse.getKommunenummer())
                                .setPostnummer(forretningsadresse.getPostnummer())
                                .setLandkode(forretningsadresse.getLandkode())
                        )
                )
                .setMetadataBuilder(Metadata.newBuilder().setMiljo(miljo))
                .build();
    }

}
