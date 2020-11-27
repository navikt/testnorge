package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class DetaljertNavnDTO extends BaseDTO<DetaljertNavn> {
    String navn1;
    String navn2;
    String navn3;
    String navn4;
    String navn5;
    String redigertNavn;

    @Override
    public DetaljertNavn toRecord() {
        var value = new DetaljertNavn();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        value.setMetadata(metadata);
        value.setNavn1(navn1);
        value.setNavn1(navn2);
        value.setNavn1(navn3);
        value.setNavn1(navn4);
        value.setNavn1(navn5);
        value.setRedigertNavn(redigertNavn);
        return value;
    }
}
