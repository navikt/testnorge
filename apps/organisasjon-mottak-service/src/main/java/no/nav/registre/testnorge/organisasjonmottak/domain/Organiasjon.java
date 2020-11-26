package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Organiasjon extends ToFlatfil {
    private final String navn;

    public Organiasjon(no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon organiasjon) {
        super(organiasjon.getMetadata());
        this.navn = organiasjon.getNavn();
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