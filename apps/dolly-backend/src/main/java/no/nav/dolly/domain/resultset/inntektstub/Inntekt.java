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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Inntekt {

    private InntektType inntektstype;
    private Double beloep;
    @Schema(description = "Startdato på perioden inntekten var opptjent",
            example = "yyyy-MM-dd",
            type = "LocalDateTime")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime startOpptjeningsperiode;
    @Schema(description = "Sluttdato på perioden inntekten var opptjent",
            example = "yyyy-MM-dd",
            type = "LocalDateTime")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime sluttOpptjeningsperiode;
    private Boolean inngaarIGrunnlagForTrekk;
    private Boolean utloeserArbeidsgiveravgift;
    @Schema(description = "Gyldige verdier finnes i kodeverket 'Fordel'")
    private String fordel;
    @Schema(description = "Gyldige verdier finnes i kodeverket 'SkatteOgAvgiftsregel'")
    private String skatteOgAvgiftsregel;
    @Schema(description = "Gyldige verdier finnes i kodeverket 'LandkoderISO2'")
    private String skattemessigBosattILand;
    @Schema(description = "Gyldige verdier finnes i kodeverket 'LandkoderISO2'")
    private String opptjeningsland;
    @Schema(description = "Gyldige verdier avhenger av inntektstypen, og finnes i kodeverkene "
            + "'Loennsbeskrivelse', 'YtelseFraOffentligeBeskrivelse', "
            + "'PensjonEllerTrygdeBeskrivelse', 'Naeringsinntektsbeskrivelse'")
    private String beskrivelse;
    @Schema(description = "OBS! kun ett av feltene i tilleggsinformasjon skal være satt")
    private Tilleggsinformasjon tilleggsinformasjon;
    @Schema(description = "F.eks. antall kilometer i kilometergodtgjørelsen")
    private Double antall;

    public enum InntektType {LOENNSINNTEKT, YTELSE_FRA_OFFENTLIGE, PENSJON_ELLER_TRYGD, NAERINGSINNTEKT}
}

