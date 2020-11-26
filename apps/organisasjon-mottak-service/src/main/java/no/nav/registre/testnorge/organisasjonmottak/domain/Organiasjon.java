package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Organiasjon {
    String orgnummer;
    String enhetstype;
    String navn;

    public Organiasjon(no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon organiasjon) {
        this.orgnummer = organiasjon.getOrgnummer();
        this.enhetstype = organiasjon.getEnhetstype();
        this.navn = organiasjon.getNavn();
    }

}