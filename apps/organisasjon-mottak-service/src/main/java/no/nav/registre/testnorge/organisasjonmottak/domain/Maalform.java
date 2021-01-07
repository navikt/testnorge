package no.nav.registre.testnorge.organisasjonmottak.domain;


public class Maalform extends ToLine {
    private final String maalform;

    public Maalform( no.nav.registre.testnorge.libs.avro.organisasjon.v1.Maalform maalform) {
        this.maalform = maalform.getMaalform();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("MÅL", 9)
                .append(8, maalform);
    }

}
