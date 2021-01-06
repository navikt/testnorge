package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Internettadresse extends ToLine {
    private final String internettadresse;

    public Internettadresse(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse internettadresse) {
        super(internettadresse.getMetadata(), uuid);
        this.internettadresse = internettadresse.getInternettadresse();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("IADR", 158)
                .setLine(8, internettadresse);
    }
}
