package no.nav.dolly.domain.resultset.inntektstub;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsInntektsinformasjon {

    @ApiModelProperty(
            position = 1,
            dataType = "Integer",
            value = "Antall måneder som denne inntektsinformasjon skal kopieres",
            example = "36")
    private Integer antallMaaneder;

    @ApiModelProperty(
            value = "Siste år-måned for gjeldene inntektsinformasjon",
            example = "yyyy-MM",
            dataType = "String",
            required = true,
            position = 2
    )
    private String sisteAarMaaned;

    @ApiModelProperty(
            value = "Organisasjonsnummer/norskIdent",
            required = true,
            position = 3
    )
    private String opplysningspliktig;

    @ApiModelProperty(
            value = "Organisasjonsnummer/norskIdent",
            required = true,
            position = 4
    )
    private String virksomhet;

    @ApiModelProperty(
            position = 5
    )
    private List<Inntekt> inntektsliste;

    @ApiModelProperty(
            position = 6
    )
    private List<Fradrag> fradragsliste;

    @ApiModelProperty(
            position = 7
    )
    private List<Forskuddstrekk> forskuddstrekksliste;

    @ApiModelProperty(
            position = 8
    )
    private List<Arbeidsforhold> arbeidsforholdsliste;

    @ApiModelProperty(
            position = 9
    )
    private List<Historikk> historikk;

    @ApiModelProperty(
            position = 10
    )
    private Integer versjon;

    public List<Historikk> getHistorikk() {
        if (isNull(historikk)) {
            historikk = new ArrayList<>();
        }
        return historikk;
    }

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class Historikk {

        @ApiModelProperty(
                position = 1
        )
        private List<Inntekt> inntektsliste;

        @ApiModelProperty(
                position = 2
        )
        private List<Fradrag> fradragsliste;

        @ApiModelProperty(
                position = 3
        )
        private List<Forskuddstrekk> forskuddstrekksliste;

        @ApiModelProperty(
                position = 4
        )
        private List<Arbeidsforhold> arbeidsforholdsliste;
    }
}
