package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;
import no.nav.registre.testnorge.organisasjonmottak.domain.Organiasjon;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class OrganiasjonDTO extends BaseDTO<Organiasjon> {
    String navn;

    @Override
    public Organiasjon toDomain() {
        var value = new no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        value.setMetadata(metadata);
        value.setNavn(navn);
        return new Organiasjon(value);
    }
}
