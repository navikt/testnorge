package no.nav.dolly.domain.resultset.pensjon;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensjonData {

    @Schema(description = "Inntekt i pensjonsopptjeningsregister (POPP)")
    private PoppInntekt inntekt;

    @Schema(description = "Data for tjenestepensjon (TP)")
    private List<TpOrdning> tp;

    @Schema(description = "Data for alderspensjon (AP)")
    private Alderspensjon alderspensjon;

    public List<TpOrdning> getTp() {
        if (isNull(tp)) {
            tp = new ArrayList<>();
        }
        return tp;
    }

    public enum TpYtelseType {
        ALDER,
        UFORE,
        GJENLEVENDE,
        BARN,
        AFP,
        UKJENT
    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PoppInntekt {
        @Schema(required = true,
                description = "Fra og med år YYYY")
        private Integer fomAar;

        @Schema(required = true,
                description = "Til og med år YYYY")
        private Integer tomAar;

        @Schema(description = "Beløp i hele kroner per år (i dagens verdi)",
                required = true)
        private Integer belop;

        @Schema(description = "Når true reduseres tidligere års pensjon i forhold til dagens kroneverdi",
                required = true)
        private Boolean redusertMedGrunnbelop;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TpOrdning {
        @Schema(required = true,
                description = "Tjenestepensjons leverandør")
        private String ordning;

        @Schema(description = "Tjenestepensjons ytelser")
        private List<TpYtelse> ytelser;

        public List<TpYtelse> getYtelser() {
            if (ytelser == null) {
                ytelser = new ArrayList<>();
            }
            return ytelser;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TpYtelse {
        @Schema(required = true,
                description = "tjenestetype")
        private TpYtelseType type;

        @Schema(required = true,
                description = "Dato innmeldt ytelse fom, kan være tidligere eller samme som iverksatt fom dato.")
        private LocalDate datoInnmeldtYtelseFom;

        @Schema(required = true,
                description = "Dato iverksatt fom")
        private LocalDate datoYtelseIverksattFom;

        @Schema(description = "Dato iverksatt tom")
        private LocalDate datoYtelseIverksattTom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alderspensjon {
        @Schema(required = true)
        private LocalDate iverksettelsesdato;

        @Schema(required = true)
        private Integer uttaksgrad;

        @Schema(required = true)
        private String sivilstand;

        @Schema
        private LocalDate sivilstatusDatoFom;
    }
}
