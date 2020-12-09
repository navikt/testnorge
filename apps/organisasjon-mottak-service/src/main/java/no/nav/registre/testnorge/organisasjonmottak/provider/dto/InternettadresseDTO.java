package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class InternettadresseDTO extends BaseDTO<Internettadresse> {
    String internettadresse;

    @Override
    public Internettadresse toRecord(String miljoe) {
        var value = new Internettadresse();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        value.setMetadata(metadata);
        value.setInternettadresse(internettadresse);
        return value;
    }
}
