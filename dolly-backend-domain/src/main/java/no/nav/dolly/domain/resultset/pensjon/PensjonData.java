package no.nav.dolly.domain.resultset.pensjon;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PensjonData {

    @ApiModelProperty(
            value = "Inntekt i pensjonsopptjeningsregister (POPP)",
            position = 1
    )
    private PoppInntekt inntekt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoppInntekt {
        @ApiModelProperty(
                value = "Fra og med år YYYY",
                required = true,
                position = 1
        )
        private Integer fomAar;

        @ApiModelProperty(
                value = "Til og med år YYYY",
                required = true,
                position = 2
        )
        private Integer tomAar;

        @ApiModelProperty(
                value = "Beløp i hele kroner per år (i dagens verdi)",
                required = true,
                position = 3
        )
        private Integer belop;

        @ApiModelProperty(
                value = "Når true reduseres tidligere års pensjon i forhold til dagens kroneverdi",
                required = true,
                position = 4
        )
        private Boolean redusertMedGrunnbelop;
    }
}