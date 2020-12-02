package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class KnytningDTO extends BaseDTO<Knytning> {
    private final String overenhetOrgnummer;
    private final String overenhetEnhetstype;

    @Override
    public Knytning toRecord(String miljoe) {
        var value = new Knytning();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        value.setMetadata(metadata);
        value.setOverenhetEnhetstype(overenhetEnhetstype);
        value.setOverenhetOrgnummer(overenhetOrgnummer);
        return value;
    }
}
