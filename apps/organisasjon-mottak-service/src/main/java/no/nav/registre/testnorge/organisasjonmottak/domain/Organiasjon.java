package no.nav.registre.testnorge.organisasjonmottak.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Organiasjon extends Base {
    String navn;

    public Organiasjon(no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon organiasjon) {
        super(organiasjon.getOrgnummer(), organiasjon.getEnhetstype());
        this.navn = organiasjon.getNavn();
    }

}