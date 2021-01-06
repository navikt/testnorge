package no.nav.registre.testnorge.organisasjonmottak.domain;


import java.time.LocalDate;

public class Naeringskode extends ToLine {
    private final String kode;
    private final LocalDate gyldighetsdato;
    private final boolean hjelpeenhet;

    public Naeringskode(String uuid, no.nav.registre.testnorge.libs.avro.organisasjon.Naeringskode naeringskode) {
        super(naeringskode.getMetadata(), uuid);
        this.kode = naeringskode.getKode();
        this.gyldighetsdato = LocalDate.of(
                naeringskode.getGyldighetsdato().getAar(),
                naeringskode.getGyldighetsdato().getMaaned(),
                naeringskode.getGyldighetsdato().getDag()
        );
        this.hjelpeenhet = naeringskode.getHjelpeenhet();
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("NACE", 23)
                .setLine(8, kode)
                .setLine(14, getDateFormatted(gyldighetsdato))
                .setLine(22, hjelpeenhet ? "J" : "N");
    }

}
