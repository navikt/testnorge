package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Ansatte extends ToFlatfil {
    private final boolean ansatte;

    public Ansatte(no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte ansatte) {
        super(ansatte.getMetadata());
        this.ansatte = ansatte.getHarAnsatte();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }


    private String toRecordLine() {
        return LineBuilder
                .newBuilder("MÅL", 9)
                .setLine(8, ansatte ? "J" : "N")
                .toString();
    }

    @Override
    public Flatfil toFlatfil() {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN());
        record.append(toRecordLine());
        flatfil.add(record);
        return flatfil;
    }
}
