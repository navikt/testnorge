package no.nav.dolly.bestilling.sykemelding.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SykemeldingRequestDTO {

    private String ident;
    private List<Aktivitet> aktivitet;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Aktivitet {
        private Integer grad;
        private LocalDate fom;
        private LocalDate tom;
    }
}
