package no.nav.dolly.domain.resultset.inntektsstub;

import java.time.ZonedDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.util.JsonZonedDateTimeDeserializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsInntektsinformasjon {

    @ApiModelProperty(
            value = "Året/måneden inntektsinformasjonen gjelder for",
            example = "yyyy-MM",
            dataType = "ZonedDateTime",
            required = true,
            position = 1
    )
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime aarMaaned;

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
                dataType = "ZonedDateTime",
                example = "yyyy-MM-dd",
                position = 2
        )
        private ZonedDateTime startdato;

        @ApiModelProperty(
                dataType = "ZonedDateTime",
                example = "yyyy-MM-dd",
                position = 3
        )
        private ZonedDateTime sluttdato;

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
                dataType = "ZonedDateTime",
                example = "yyyy-MM-dd",
                position = 9
        )
        private ZonedDateTime sisteLoennsendringsdato;

        @ApiModelProperty(
                dataType = "ZonedDateTime",
                example = "yyyy-MM-dd",
                position = 10
        )
        private ZonedDateTime sisteDatoForStillingsprosentendring;
    }
}
