package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Epost extends ToFlatfil {
    private final String epost;

    public Epost(no.nav.registre.testnorge.libs.avro.organisasjon.Epost epost) {
        super(epost.getMetadata());
        this.epost = epost.getEpost();
    }

    private String toRecord() {
        return LineBuilder
                .newBuilder("EPOS", 158)
                .setLine(8, epost)
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
        record.append(toRecord());
        flatfil.add(record);
        return flatfil;
    }
}
