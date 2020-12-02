package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Knyttning {
    private final String overenhetOrgnummer;
    private final String overenhetEnhetstype;
    private final String underenhetOrgnummer;
    private final String underenhetEnhetstype;

    public Knyttning(no.nav.registre.testnorge.libs.avro.organisasjon.Knyttning knyttning) {
        this.overenhetOrgnummer = knyttning.getOverenhetOrgnummer();
        this.underenhetOrgnummer = knyttning.getUnderenheterOrgnummer();
        this.underenhetEnhetstype = knyttning.getUnderenheterEnhetstype();
        this.overenhetEnhetstype = knyttning.getOverenhetEnhetstype();
    }

}
