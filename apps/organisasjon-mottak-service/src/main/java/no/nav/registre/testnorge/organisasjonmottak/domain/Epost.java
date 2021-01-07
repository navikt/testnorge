package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Epost extends ToLine {
    private final String epost;

    public Epost(no.nav.registre.testnorge.libs.avro.organisasjon.v1.Epost epost) {
        this.epost = epost.getEpost();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("EPOS", 158)
                .append(8, epost);
    }
}