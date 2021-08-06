package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Telefon extends ToLine {
    private final String tlf;

    public Telefon(no.nav.testnav.libs.avro.organisasjon.v1.Telefon telefon) {
        this.tlf = telefon.getTlf();
    }


    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("TFON", 21)
                .append(8, tlf);
    }
}