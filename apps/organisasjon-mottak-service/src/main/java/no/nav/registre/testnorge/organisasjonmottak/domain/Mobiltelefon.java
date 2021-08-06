package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Mobiltelefon extends ToLine {
    private final String tlf;

    public Mobiltelefon(no.nav.testnav.libs.avro.organisasjon.v1.Telefon mobiltelefon) {
        this.tlf = mobiltelefon.getTlf();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("MTLF", 21)
                .append(8, tlf);
    }

}
