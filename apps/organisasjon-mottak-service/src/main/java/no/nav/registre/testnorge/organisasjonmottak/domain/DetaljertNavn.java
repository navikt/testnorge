package no.nav.registre.testnorge.organisasjonmottak.domain;

public class DetaljertNavn extends ToFlatfil {
    private final String navn1;
    private final String navn2;
    private final String navn3;
    private final String navn4;
    private final String navn5;
    private final String redigertNavn;

    public DetaljertNavn(no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn detaljertNavn) {
        super(detaljertNavn.getMetadata());
        this.navn1 = detaljertNavn.getNavn1();
        this.navn2 = detaljertNavn.getNavn2();
        this.navn3 = detaljertNavn.getNavn3();
        this.navn4 = detaljertNavn.getNavn4();
        this.navn5 = detaljertNavn.getNavn5();
        this.redigertNavn = detaljertNavn.getRedigertNavn();
    }

    private String createNavn() {
        return LineBuilder
                .newBuilder("NAVN", 219)
                .setLine(8, navn1)
                .setLine(43, navn2)
                .setLine(78, navn3)
                .setLine(113, navn4)
                .setLine(148, navn5)
                .setLine(183, redigertNavn)
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
        record.append(createNavn());
        flatfil.add(record);
        return flatfil;
    }
}
