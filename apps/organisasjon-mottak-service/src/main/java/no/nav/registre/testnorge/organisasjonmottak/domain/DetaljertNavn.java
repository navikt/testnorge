package no.nav.registre.testnorge.organisasjonmottak.domain;

public class DetaljertNavn extends ToLine {
    private final String navn1;
    private final String navn2;
    private final String navn3;
    private final String navn4;
    private final String navn5;
    private final String redigertNavn;

    public DetaljertNavn(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn detaljertNavn) {
        super(detaljertNavn.getMetadata(), uuid);
        this.navn1 = detaljertNavn.getNavn1();
        this.navn2 = detaljertNavn.getNavn2();
        this.navn3 = detaljertNavn.getNavn3();
        this.navn4 = detaljertNavn.getNavn4();
        this.navn5 = detaljertNavn.getNavn5();
        this.redigertNavn = detaljertNavn.getRedigertNavn();
    }


    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("NAVN", 219)
                .setLine(8, navn1)
                .setLine(43, navn2)
                .setLine(78, navn3)
                .setLine(113, navn4)
                .setLine(148, navn5)
                .setLine(183, redigertNavn);
    }
}
