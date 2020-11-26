package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Navn extends Base {
    String navn;

    public Navn(no.nav.registre.testnorge.libs.avro.organiasjon.Navn navn) {
        super(navn.getMetadata());
        this.navn = navn.getNavn();
    }
}
