package no.nav.registre.testnorge.organisasjonmottak.domain;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Internettadresse extends ToFlatfil {
    private final String internettadresse;

    public Internettadresse(Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse internettadresse) {
        super(metadata);
        this.internettadresse = internettadresse.getInternettadresse();
    }

    private String toInternettadresse() {
        return LineBuilder
                .newBuilder("IADR", 158)
                .setLine(8, internettadresse)
                .toString();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    @Override
    public Flatfil toFlatfil() {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN());
        record.append(toInternettadresse());
        flatfil.add(record);
        return flatfil;
    }
}
