package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.Forretningsadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class ForretningsadresseDTO extends BaseDTO<Forretningsadresse> {
    String postnummer;
    String landkode;
    String kommunenummer;
    String poststed;
    String postadresse1;
    String postadresse2;
    String postadresse3;
    String linjenummer;
    String vegadresseId;

    @Override
    public Forretningsadresse toRecord(String miljoe) {
        var value = new Forretningsadresse();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        value.setMetadata(metadata);
        value.setPostnummer(postnummer);
        value.setLandkode(landkode);
        value.setKommunenummer(kommunenummer);
        value.setPoststed(poststed);
        value.setPostadresse1(postadresse1);
        value.setPostadresse2(postadresse2);
        value.setPostadresse3(postadresse3);
        value.setLinjenummer(linjenummer);
        value.setVegadresseId(vegadresseId);
        return value;
    }
}
