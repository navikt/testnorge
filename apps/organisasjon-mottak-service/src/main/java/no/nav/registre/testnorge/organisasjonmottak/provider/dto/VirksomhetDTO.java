package no.nav.registre.testnorge.organisasjonmottak.provider.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.avro.organisasjon.Virksomhet;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VirksomhetDTO {
    String enhetstype;
    String orgnummer;

    public Virksomhet toRecord() {
        var value = new Virksomhet();
        value.setEnhetstype(enhetstype);
        value.setOrgnummer(orgnummer);
        return value;
    }
}
