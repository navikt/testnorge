package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.time.LocalDate;

public class Stiftelsesdato extends ToLine {
    private final LocalDate localDate;

    public Stiftelsesdato(no.nav.testnav.libs.avro.organisasjon.v1.Stiftelsesdato stiftelsesdato) {
        localDate = LocalDate.of(
                stiftelsesdato.getDato().getAar(),
                stiftelsesdato.getDato().getMaaned(),
                stiftelsesdato.getDato().getDag()
        );
    }

    @Override
    FlatfilValueBuilder builder() {
        return FlatfilValueBuilder
                .newBuilder("STID", 16)
                .append(8, getDateFormatted(localDate));
    }
}
