package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class AnsatteDTO extends BaseDTO<Ansatte> {
    Boolean harAnsatte;

    @Override
    public Ansatte toRecord(String miljoe) {
        var ansatte = new Ansatte();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        ansatte.setMetadata(metadata);
        ansatte.setHarAnsatte(harAnsatte != null && harAnsatte);
        return ansatte;
    }
}
