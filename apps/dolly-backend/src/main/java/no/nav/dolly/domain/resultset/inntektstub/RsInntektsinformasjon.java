package no.nav.dolly.domain.resultset.inntektstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsInntektsinformasjon {

    @Schema(type = "Integer",
            description = "Antall måneder som denne inntektsinformasjon skal kopieres",
            example = "36")
    private Integer antallMaaneder;

    @Schema(description = "Siste år-måned for gjeldene inntektsinformasjon",
            example = "yyyy-MM",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String sisteAarMaaned;

    @Schema(description = "Organisasjonsnummer/norskIdent",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String opplysningspliktig;

    @Schema(description = "Organisasjonsnummer/norskIdent",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String virksomhet;

    private List<Inntekt> inntektsliste;

    private List<Fradrag> fradragsliste;

    private List<Forskuddstrekk> forskuddstrekksliste;

    private List<Arbeidsforhold> arbeidsforholdsliste;

    private List<Historikk> historikk;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime rapporteringsdato;

    private Integer versjon;

    public List<Inntekt> getInntektsliste() {
        if (isNull(inntektsliste)) {
            inntektsliste = new ArrayList<>();
        }
        return inntektsliste;
    }

    public List<Fradrag> getFradragsliste() {
        if (isNull(fradragsliste)) {
            fradragsliste = new ArrayList<>();
        }
        return fradragsliste;
    }

    public List<Forskuddstrekk> getForskuddstrekksliste() {
        if (isNull(forskuddstrekksliste)) {
            forskuddstrekksliste = new ArrayList<>();
        }
        return forskuddstrekksliste;
    }

    public List<Arbeidsforhold> getArbeidsforholdsliste() {
        return arbeidsforholdsliste;
    }

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

        private Double beloep;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Fradragbeskrivelse'")
        private String beskrivelse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forskuddstrekk {

        private Double beloep;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Forskuddstrekkbeskrivelse'")
        private String beskrivelse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsforhold {

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Arbeidsforholdstyper'")
        private String arbeidsforholdstype;

        @Schema(type = "LocalDateTime",
                example = "yyyy-MM-dd")
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime startdato;

        @Schema(type = "LocalDateTime",
                example = "yyyy-MM-dd")
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime sluttdato;

        private Double antallTimerPerUkeSomEnFullStillingTilsvarer;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Avlønningstyper'")
        private String avloenningstype;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Yrker'")
        private String yrke;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Arbeidstidsordninger'")
        private String arbeidstidsordning;

        private Double stillingsprosent;

        @Schema(type = "LocalDateTime",
                example = "yyyy-MM-dd")
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime sisteLoennsendringsdato;

        @Schema(type = "LocalDateTime",
                example = "yyyy-MM-dd")
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime sisteDatoForStillingsprosentendring;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Historikk {

        private List<Inntekt> inntektsliste;

        private List<Fradrag> fradragsliste;

        private List<Forskuddstrekk> forskuddstrekksliste;

        private List<Arbeidsforhold> arbeidsforholdsliste;

        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime rapporteringsdato;
    }
}
