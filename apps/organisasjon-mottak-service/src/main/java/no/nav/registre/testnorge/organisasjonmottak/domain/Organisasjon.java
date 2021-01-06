package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Organisasjon extends ToLine {
    private final String navn;

    public Organisasjon(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon organisasjon) {
        super(organisasjon.getMetadata(), uuid);
        this.navn = organisasjon.getNavn();
    }

    @Override
    public boolean isUpdatable() {
        return false;
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("NAVN", 219)
                .setLine(8, navn)
                .setLine(183, navn);
    }

}