package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Internettadresse extends ToFlatfil {
    private final String internettadresse;

    public Internettadresse(no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse internettadresse) {
        super(internettadresse.getMetadata());
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
