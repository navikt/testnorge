package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Knytning extends ToFlatfil {
    private final String juridiskEnhet;

    public Knytning(no.nav.registre.testnorge.libs.avro.organisasjon.Knytning knytning) {
        super(knytning.getMetadata());
        juridiskEnhet = knytning.getJuridiskEnhet();
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    private String toRecordLine() {
        return LineBuilder
                .newBuilder(this.getEnhetstype(), 66)
                .setLine(5, "SSY")
                .setLine(8, "K")
                .setLine(9, "D")
                .setLine(41, juridiskEnhet)
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
