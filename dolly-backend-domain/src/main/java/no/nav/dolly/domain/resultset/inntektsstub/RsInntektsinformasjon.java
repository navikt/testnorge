package no.nav.dolly.domain.resultset.inntektsstub;

import java.time.LocalDateTime;
import java.util.List;

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
public class RsInntektsinformasjon {

    @ApiModelProperty(
            value = "Året/måneden inntektsinformasjonen gjelder for",
            example = "yyyy-MM",
            dataType = "LocalDateTime",
            required = true,
            position = 1
    )
    private LocalDateTime aarMaaned;

    @ApiModelProperty(
            value = "Organisasjonsnummer/norskIdent",
            required = true,
            position = 2
    )
    private String opplysningspliktig;

    @ApiModelProperty(
            value = "Organisasjonsnummer/norskIdent",
            required = true,
            position = 3
    )
    private String virksomhet;

    @ApiModelProperty(
            position = 4
    )
    private List<Inntekt> inntektsliste;

    @ApiModelProperty(
            position = 5
    )
    private List<Fradrag> fradragsliste;

    @ApiModelProperty(
            position = 6
    )
    private List<Forskuddstrekk> forskuddstrekksliste;

    @ApiModelProperty(
            position = 7
    )
    private List<Arbeidsforhold> arbeidsforholdsliste;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fradrag {

        @ApiModelProperty(
                position = 1
        )
        private Double beloep;

        @ApiModelProperty(
                value = "Gyldige verdier finnes i kodeverket 'Fradragbeskrivelse'",
                position = 2
        )
        private String beskrivelse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forskuddstrekk {

        @ApiModelProperty(
                position = 1
        )
        private Double beloep;

        @ApiModelProperty(
                value = "Gyldige verdier finnes i kodeverket 'Forskuddstrekkbeskrivelse'",
                position = 2
        )
        private String beskrivelse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsforhold {

        @ApiModelProperty(
                value = "Gyldige verdier finnes i kodeverket 'Arbeidsforholdstyper'",
                position = 1
        )
        private String arbeidsforholdstype;

        @ApiModelProperty(
                dataType = "LocalDateTime",
                example = "yyyy-MM-dd",
                position = 2
        )
        private LocalDateTime startdato;

        @ApiModelProperty(
                dataType = "LocalDateTime",
                example = "yyyy-MM-dd",
                position = 3
        )
        private LocalDateTime sluttdato;

        @ApiModelProperty(
                position = 4
        )
        private Double antallTimerPerUkeSomEnFullStillingTilsvarer;

        @ApiModelProperty(
                value = "Gyldige verdier finnes i kodeverket 'Avlønningstyper'",
                position = 5
        )
        private String avloenningstype;

        @ApiModelProperty(
                value = "Gyldige verdier finnes i kodeverket 'Yrker'",
                position = 6
        )
        private String yrke;

        @ApiModelProperty(
                value = "Gyldige verdier finnes i kodeverket 'Arbeidstidsordninger'",
                position = 7
        )
        private String arbeidstidsordning;

        @ApiModelProperty(
                position = 8
        )
        private Double stillingsprosent;

        @ApiModelProperty(
                dataType = "LocalDateTime",
                example = "yyyy-MM-dd",
                position = 9
        )
        private LocalDateTime sisteLoennsendringsdato;

        @ApiModelProperty(
                dataType = "LocalDateTime",
                example = "yyyy-MM-dd",
                position = 10
        )
        private LocalDateTime sisteDatoForStillingsprosentendring;
    }
}
