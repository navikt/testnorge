package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import no.nav.registre.testnorge.libs.avro.organiasjon.Metadata;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public abstract class Base {
    private final String orgnummer;
    private final String enhetstype;

    public Base(Metadata metadata) {
        this.orgnummer = metadata.getOrgnummer();
        this.enhetstype = metadata.getEnhetstype();
    }
}
