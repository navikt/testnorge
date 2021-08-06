package no.nav.registre.testnorge.organisasjonmottak.domain;

public class DetaljertNavn extends ToLine {
    private final String navn1;
    private final String navn2;
    private final String navn3;
    private final String navn4;
    private final String navn5;
    private final String redigertNavn;

    public DetaljertNavn(no.nav.testnav.libs.avro.organisasjon.v1.DetaljertNavn detaljertNavn) {
        this.navn1 = detaljertNavn.getNavn1();
        this.navn2 = detaljertNavn.getNavn2();
        this.navn3 = detaljertNavn.getNavn3();
        this.navn4 = detaljertNavn.getNavn4();
        this.navn5 = detaljertNavn.getNavn5();
        this.redigertNavn = detaljertNavn.getRedigertNavn();
    }


    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("NAVN", 219)
                .append(8, navn1)
                .append(43, navn2)
                .append(78, navn3)
                .append(113, navn4)
                .append(148, navn5)
                .append(183, redigertNavn);
    }
}
