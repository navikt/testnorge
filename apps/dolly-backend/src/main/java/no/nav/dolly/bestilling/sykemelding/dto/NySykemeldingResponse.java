package no.nav.dolly.bestilling.sykemelding.dto;

import java.time.LocalDate;
import java.util.List;

public record NySykemeldingResponse(
        String error,
        String sykmeldingId,
        List<Aktivitet> aktivitet,
        String ident
) {
    public record Aktivitet(
            LocalDate fom,
            LocalDate tom
    ) {
    }
}
