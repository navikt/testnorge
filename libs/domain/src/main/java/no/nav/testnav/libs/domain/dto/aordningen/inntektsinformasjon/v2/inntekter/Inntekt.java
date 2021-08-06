package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inntekt {

    @ApiModelProperty(
            value = "unik id som identifiserer en eksisterende inntekt, settes til null om det skal opprettes en ny inntekt",
            position = 0
    )
    private Long id;
    @ApiModelProperty(
            value = "",
            position = 1
    )
    private Inntektstype inntektstype;
    @ApiModelProperty(
            value = "",
            position = 2
    )
    private double beloep;
    @ApiModelProperty(
            value = "startdato på perioden inntekten var opptjent",
            example = "yyyy-MM-dd",
            position = 3
    )
    private LocalDate startOpptjeningsperiode;
    @ApiModelProperty(
            value = "sluttdato på perioden inntekten var opptjent",
            example = "yyyy-MM-dd",
            position = 4
    )
    private LocalDate sluttOpptjeningsperiode;
    @ApiModelProperty(
            value = "",
            position = 5
    )
    private boolean inngaarIGrunnlagForTrekk;
    @ApiModelProperty(
            value = "",
            position = 6
    )
    private boolean utloeserArbeidsgiveravgift;
    @ApiModelProperty(
            value = "",
            position = 7
    )
    private String fordel;
    @ApiModelProperty(
            value = "gyldige verdier ligger i kodeverket 'Fordel'",
            position = 8
    )
    private String skatteOgAvgiftsregel;
    @ApiModelProperty(
            value = "",
            position = 9
    )
    private String skattemessigBosattILand;
    @ApiModelProperty(
            value = "",
            position = 10
    )
    private String opptjeningsland;
    @ApiModelProperty(
            value = "gyldige verdier avhenger av inntektstypen. gyldige verdier ligger i kodeverkene 'Loennsbeskrivelse', 'YtelseFraOffentligeBeskrivelse', 'PensjonEllerTrygdeBeskrivelse', 'Naeringsinntektsbeskrivelse'",
            position = 11
    )
    private String beskrivelse;
    @ApiModelProperty(
            value = "OBS! kun ett av feltene i tilleggsinformasjon kan være satt",
            position = 12
    )
    private Tilleggsinformasjon tilleggsinformasjon;
    @ApiModelProperty(
            value = "",
            position = 13
    )
    private List<Avvik> avvik;
    @ApiModelProperty(
            value = "f.eks antall kilometer i kilometergodtgjørelsen",
            position = 14
    )
    private Double antall;
    @ApiModelProperty(
            value = "menneskelig lesbar feilmelding. Ligge kun i responsen fra inntektstub om noe er galt med dette objektet.",
            position = 15
    )
    private String feilmelding;
}
