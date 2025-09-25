package no.nav.dolly.bestilling.sykemelding.domain.dto;

import java.time.LocalDate;
import java.util.List;

public record NySykemeldingResponseDTO(
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
