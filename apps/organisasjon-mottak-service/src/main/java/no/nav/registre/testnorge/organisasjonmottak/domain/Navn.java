package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Navn extends ToLine {
    private final String navn;

    public Navn(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Navn navn) {
        super(navn.getMetadata(), uuid);
        this.navn = navn.getNavn();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("NAVN", 219)
                .setLine(8, navn)
                .setLine(183, navn);
    }

}
