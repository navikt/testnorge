package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlInnflytting {

    private Folkeregistermetadata folkeregistermetadata;
    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
    private String kilde;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregistermetadata {

        private LocalDate ajourholdstidspunkt;
        private LocalDate gyldighetstidspunkt;
        private LocalDate opphoerstidspunkt;
    }
}