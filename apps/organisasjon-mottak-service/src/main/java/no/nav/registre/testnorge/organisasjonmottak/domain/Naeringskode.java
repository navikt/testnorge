package no.nav.registre.testnorge.organisasjonmottak.domain;


import java.time.LocalDate;

public class Naeringskode extends ToLine {
    private final String kode;
    private final LocalDate gyldighetsdato;
    private final boolean hjelpeenhet;

    public Naeringskode( no.nav.registre.testnorge.libs.avro.organisasjon.v1.Naeringskode naeringskode) {
        this.kode = naeringskode.getKode();
        this.gyldighetsdato = LocalDate.of(
                naeringskode.getGyldighetsdato().getAar(),
                naeringskode.getGyldighetsdato().getMaaned(),
                naeringskode.getGyldighetsdato().getDag()
        );
        this.hjelpeenhet = naeringskode.getHjelpeenhet();
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("NACE", 23)
                .append(8, kode)
                .append(14, getDateFormatted(gyldighetsdato))
                .append(22, hjelpeenhet ? "J" : "N");
    }

}
