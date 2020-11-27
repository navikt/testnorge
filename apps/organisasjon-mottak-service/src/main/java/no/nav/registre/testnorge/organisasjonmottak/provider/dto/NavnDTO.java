package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;
import no.nav.registre.testnorge.libs.avro.organiasjon.Navn;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class NavnDTO extends BaseDTO<Navn> {
    String navn;

    @Override
    public Navn toRecord() {
        var value = new Navn();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        value.setMetadata(metadata);
        value.setNavn(navn);
        return value;
    }
}
