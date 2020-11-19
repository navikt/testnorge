package no.nav.registre.testnorge.organisasjonmottakservice.domain;

import lombok.Value;

@Value
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