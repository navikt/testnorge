package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Internettadresse extends ToLine {
    private final String internettadresse;

    public Internettadresse(no.nav.testnav.libs.avro.organisasjon.v1.Internettadresse internettadresse) {
        this.internettadresse = internettadresse.getInternettadresse();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("IADR", 158)
                .append(8, internettadresse);
    }
}
