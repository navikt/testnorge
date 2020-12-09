package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.Naeringskode;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class NaeringskodeDTO extends BaseDTO<Naeringskode> {
    String kode;
    LocalDate gyldighetsdato;
    Boolean hjelpeenhet;

    @Override
    public Naeringskode toRecord(String miljoe) {
        var value = new Naeringskode();
        var metadata = new Metadata();
        metadata.setOrgnummer(getOrgnummer());
        metadata.setEnhetstype(getEnhetstype());
        metadata.setMiljo(miljoe);
        value.setMetadata(metadata);
        value.setKode(kode);
        value.setGyldighetsdato(toDato(gyldighetsdato));
        value.setHjelpeenhet(hjelpeenhet != null && hjelpeenhet);
        return value;
    }
}
