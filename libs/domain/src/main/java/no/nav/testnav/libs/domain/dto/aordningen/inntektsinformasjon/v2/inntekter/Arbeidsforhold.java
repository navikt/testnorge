package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arbeidsforhold {

    @ApiModelProperty(
            value = "unik id som identifiserer et eksisterende arbeidsforhold, settes til null om det skal opprettes et nytt arbeidsforhold",
            position = 0
    )
    private Long id;
    @ApiModelProperty(
            value = "gyldige verdier ligger i kodeverket 'Arbeidsforholdstyper'",
            position = 1
    )
    private String arbeidsforholdstype;
    @ApiModelProperty(
            example = "yyyy-MM-dd",
            position = 2
    )
    private LocalDate startdato;
    @ApiModelProperty(
            example = "yyyy-MM-dd",
            position = 3
    )
    private LocalDate sluttdato;
    @ApiModelProperty(
            value = "",
            position = 4
    )
    private Double antallTimerPerUkeSomEnFullStillingTilsvarer;
    @ApiModelProperty(
            value = "gyldige verdier ligger i kodeverket 'Avlønningstyper'",
            position = 5
    )
    private String avloenningstype;
    @ApiModelProperty(
            value = "gyldige verdier ligger i kodeverket 'Yrker'",
            position = 6
    )
    private String yrke;
    @ApiModelProperty(
            value = "gyldige verdier ligger i kodeverket 'Arbeidstidsordninger'",
            position = 7
    )
    private String arbeidstidsordning;
    @ApiModelProperty(
            value = "",
            position = 8
    )
    private Double stillingsprosent;
    @ApiModelProperty(
            example = "yyyy-MM-dd",
            position = 9
    )
    private LocalDate sisteLoennsendringsdato;
    @ApiModelProperty(
            example = "yyyy-MM-dd",
            position = 10
    )
    private LocalDate sisteDatoForStillingsprosentendring;
    @ApiModelProperty(
            value = "menneskelig lesbar feilmelding. Ligge kun i responsen fra inntektstub om noe er galt med dette objektet.",
            position = 11
    )
    private String feilmelding;
}
