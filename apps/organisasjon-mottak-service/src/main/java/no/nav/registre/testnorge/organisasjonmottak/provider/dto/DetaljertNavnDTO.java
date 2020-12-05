package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

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
    public DetaljertNavn toRecord(String miljoe) {
        var value = new DetaljertNavn();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        value.setMetadata(metadata);
        value.setNavn1(navn1);
        value.setNavn2(navn2);
        value.setNavn3(navn3);
        value.setNavn4(navn4);
        value.setNavn5(navn5);
        value.setRedigertNavn(redigertNavn);
        return value;
    }
}
