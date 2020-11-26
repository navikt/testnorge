package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Navn extends ToFlatfil {
    private final String navn;

    public Navn(no.nav.registre.testnorge.libs.avro.organiasjon.Navn navn) {
        super(navn.getMetadata());
        this.navn = navn.getNavn();
    }

    private String createNavn() {
        LineBuilder builder = LineBuilder.newBuilder("NAVN", 219);
        builder.setLine(8, navn);
        builder.setLine(183, navn);
        return builder.toString();
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
        record.append(createNavn());
        flatfil.add(record);
        return flatfil;
    }
}
