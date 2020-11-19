package no.nav.registre.testnorge.organisasjonmottakservice.domain;

import lombok.Value;

@Value
public class Knyttning {
    String overenhetOrgnummer;
    String overenhetEnhetstype;
    String underenhetOrgnummer;
    String underenhetEnhetstype;

    public Knyttning(no.nav.registre.testnorge.libs.avro.organiasjon.Knyttning knyttning) {
        this.overenhetOrgnummer = knyttning.getOverenhetOrgnummer();
        this.underenhetOrgnummer = knyttning.getUnderenheterOrgnummer();
        this.underenhetEnhetstype = knyttning.getUnderenheterEnhetstype();
        this.overenhetEnhetstype = knyttning.getOverenhetEnhetstype();
    }

}
