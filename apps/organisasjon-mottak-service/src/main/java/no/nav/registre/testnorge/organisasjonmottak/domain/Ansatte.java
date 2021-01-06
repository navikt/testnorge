package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Ansatte extends ToLine {
    private final boolean ansatte;

    public Ansatte(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte ansatte) {
        super(ansatte.getMetadata(), uuid);
        this.ansatte = ansatte.getHarAnsatte();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("MÅL", 9)
                .setLine(8, ansatte ? "J" : "N");
    }
}
