package no.nav.registre.testnorge.organisasjonmottak.domain;

public class Virksomhet {
    private final String orgnummer;
    private final String enhetstype;

    public Virksomhet(no.nav.registre.testnorge.libs.avro.organisasjon.Virksomhet virksomhet) {
        this.orgnummer = virksomhet.getOrgnummer();
        this.enhetstype = virksomhet.getEnhetstype();
    }

    String toRecordLine() {
        return LineBuilder
                .newBuilder(enhetstype, 66)
                .setLine(5, "SSY")
                .setLine(8, "K")
                .setLine(9, "D")
                .setLine(41, orgnummer)
                .toString();
    }
}
