package no.nav.registre.testnorge.organisasjonmottak.provider.dto;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class EpostDTO extends BaseDTO<Epost> {
    String epost;

    @Override
    public Epost toRecord(String miljoe) {
        var value = new Epost();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        value.setMetadata(metadata);
        value.setEpost(epost);
        return value;
    }
}
