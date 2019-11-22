package no.nav.dolly.domain.resultset.inntektsstub;

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
public class Inntekt {

    public enum InntektType {LOENNSINNTEKT, YTELSE_FRA_OFFENTLIGE, PENSJON_ELLER_TRYGD, NAERINGSINNTEKT}

    @ApiModelProperty(
            position = 1
    )
    private InntektType inntektstype;

    @ApiModelProperty(
            position = 2
    )
    private double beloep;

    @ApiModelProperty(
            value = "Startdato på perioden inntekten var opptjent",
            example = "yyyy-MM-dd",
            dataType = "LocalDateTime",
            position = 3
    )
    private LocalDateTime startOpptjeningsperiode;

    @ApiModelProperty(
            value = "Sluttdato på perioden inntekten var opptjent",
            example = "yyyy-MM-dd",
            dataType = "LocalDateTime",
            position = 4
    )
    private LocalDateTime sluttOpptjeningsperiode;

    @ApiModelProperty(
            position = 5
    )
    private boolean inngaarIGrunnlagForTrekk;

    @ApiModelProperty(
            position = 6
    )
    private boolean utloeserArbeidsgiveravgift;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverket 'Fordel'",
            position = 7
    )
    private String fordel;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverket 'SkatteOgAvgiftsregel'",
            position = 8
    )
    private String skatteOgAvgiftsregel;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverket 'LandkoderISO2'",
            position = 9
    )
    private String skattemessigBosattILand;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverket 'LandkoderISO2'",
            position = 10
    )
    private String opptjeningsland;

    @ApiModelProperty(
            value = "Gyldige verdier avhenger av inntektstypen, og finnes i kodeverkene 'Loennsbeskrivelse', 'YtelseFraOffentligeBeskrivelse', 'PensjonEllerTrygdeBeskrivelse', 'Naeringsinntektsbeskrivelse'",
            position = 11
    )
    private String beskrivelse;

    @ApiModelProperty(
            value = "OBS! kun ett av feltene i tilleggsinformasjon skal være satt",
            position = 12
    )
    private Tilleggsinformasjon tilleggsinformasjon;

    @ApiModelProperty(
            value = "F.eks. antall kilometer i kilometergodtgjørelsen",
            position = 13
    )
    private double antall;
}

