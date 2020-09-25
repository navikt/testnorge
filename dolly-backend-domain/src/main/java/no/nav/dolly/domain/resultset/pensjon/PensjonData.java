package no.nav.dolly.domain.resultset.pensjon;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensjonData {

    @Schema(description = "Inntekt i pensjonsopptjeningsregister (POPP)")
    private PoppInntekt inntekt;

    @Getter
    @Setter
    @NoArgsConstructor
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
}