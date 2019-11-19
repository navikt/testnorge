package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsavtale {

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'Arbeidstidsordninger'",
            dataType = "String",
            required = true,
            position = 1
    )
    private String arbeidstidsordning;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'Avlønningstyper'",
            dataType = "String",
            required = true,
            position = 2
    )
    private String avloenningstype;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'Yrker'",
            dataType = "String",
            required = true,
            position = 3
    )
    private String yrke;

    @ApiModelProperty(
            position = 4
    )
    private BigDecimal avtaltArbeidstimerPerUke;

    @ApiModelProperty(
            position = 5
    )
    private BigDecimal stillingsprosent;

    @ApiModelProperty(
            position = 6
    )
    private BigDecimal antallKonverterteTimer;

    @ApiModelProperty(
            dataType = "LocalDateTime",
            position = 7
    )
    private LocalDateTime endringsdatoStillingsprosent;

    @ApiModelProperty(
            dataType = "LocalDateTime",
            position = 8
    )
    private LocalDateTime sisteLoennsendringsdato;
}
