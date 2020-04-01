package no.nav.dolly.domain.resultset.inntektsmeldingstub;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsInntektsmelding {

    private List<InntektMelding> inntekter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InntektMelding {

        @ApiModelProperty(
                value = "Organisasjonsnummer/norskIdent",
                required = true,
                position = 1
        )
        private String virksomhetsnummer;

        @ApiModelProperty(
                value = "Dato",
                required = true,
                position = 2
        )
        private LocalDateTime dato;

        @ApiModelProperty(
                value = "Beloep",
                required = true,
                position = 3
        )
        private Double beloep;
    }
}
