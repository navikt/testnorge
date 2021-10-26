package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class PdlOpplysning {

    public enum Master {FREG, PDL}

    private String kilde;
    private Master master;
    private Folkeregistermetadata folkeregistermetadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Folkeregistermetadata {

        private LocalDate ajourholdstidspunkt;
        private LocalDate gyldighetstidspunkt;
        private LocalDate opphoerstidspunkt;
        private Boolean gjeldende;
    }
}