package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Ansatte extends ToLine {
    private final boolean ansatte;

    public Ansatte(no.nav.testnav.libs.avro.organisasjon.v1.Ansatte ansatte) {
        this.ansatte = ansatte.getHarAnsatte();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("MÅL", 9)
                .append(8, ansatte ? "J" : "N");
    }
}
