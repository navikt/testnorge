package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Knytning extends ToLine {
    private final String juridiskEnhet;

    public Knytning(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Knytning knytning) {
        super(knytning.getMetadata(), uuid);
        juridiskEnhet = knytning.getJuridiskEnhet();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder(this.getEnhetstype(), 66)
                .setLine(5, "SSY")
                .setLine(8, "K")
                .setLine(9, "D")
                .setLine(41, juridiskEnhet);
    }
}
