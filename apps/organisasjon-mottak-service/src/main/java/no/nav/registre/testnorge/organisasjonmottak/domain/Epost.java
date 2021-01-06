package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Epost extends ToLine {
    private final String epost;

    public Epost(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Epost epost) {
        super(epost.getMetadata(), uuid);
        this.epost = epost.getEpost();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("EPOS", 158)
                .setLine(8, epost);
    }
}
