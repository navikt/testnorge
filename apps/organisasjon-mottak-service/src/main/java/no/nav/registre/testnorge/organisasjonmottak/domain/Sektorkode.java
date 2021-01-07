package no.nav.registre.testnorge.organisasjonmottak.domain;


public class Sektorkode extends ToLine {
    private final String kode;

    public Sektorkode(no.nav.registre.testnorge.libs.avro.organisasjon.v1.Sektorkode sektorkode) {
        this.kode = sektorkode.getSektorkode();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("ISEK", 12)
                .append(8, kode);
    }
}
