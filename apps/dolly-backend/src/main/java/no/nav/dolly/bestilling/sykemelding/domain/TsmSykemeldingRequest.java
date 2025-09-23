package no.nav.dolly.bestilling.sykemelding.domain;

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
public class TsmSykemeldingRequest {

    private String ident;
    private List<Aktivitet> aktivitet;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Aktivitet {
        private LocalDate fom;
        private LocalDate tom;
    }
}
