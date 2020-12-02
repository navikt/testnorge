package no.nav.registre.testnorge.organisasjonmottak.domain;

public class organisasjon extends ToFlatfil {
    private final String navn;

    public organisasjon(no.nav.registre.testnorge.libs.avro.organisasjon.organisasjon organisasjon) {
        super(organisasjon.getMetadata());
        this.navn = organisasjon.getNavn();
    }

    private String createNavn() {
        return LineBuilder
                .newBuilder("NAVN", 219)
                .setLine(8, navn)
                .setLine(183, navn)
                .toString();
    }

    @Override
    public boolean isUpdate() {
        return false;
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