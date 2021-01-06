package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;

public class Stiftelsesdato extends ToLine {
    private final LocalDate localDate;

    public Stiftelsesdato(String uuid, Metadata metadata, no.nav.registre.testnorge.libs.avro.organisasjon.Stiftelsesdato stiftelsesdato) {
        super(metadata, uuid);
        localDate = LocalDate.of(
                stiftelsesdato.getDato().getAar(),
                stiftelsesdato.getDato().getMaaned(),
                stiftelsesdato.getDato().getDag()
        );
    }

    @Override
    ValueBuilder builder() {
        return ValueBuilder
                .newBuilder("STID", 16)
                .setLine(8, getDateFormatted(localDate));
    }
}
