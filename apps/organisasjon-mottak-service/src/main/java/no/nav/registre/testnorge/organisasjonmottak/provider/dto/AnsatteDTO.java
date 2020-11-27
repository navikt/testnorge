package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;
import no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class AnsatteDTO extends BaseDTO<Ansatte> {
    Boolean harAnsatte;

    @Override
    public Ansatte toDomain() {
        var ansatte = new no.nav.registre.testnorge.libs.avro.organiasjon.Ansatte();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        ansatte.setMetadata(metadata);
        ansatte.setHarAnsatte(harAnsatte != null && harAnsatte);
        return new Ansatte(ansatte);
    }
}
