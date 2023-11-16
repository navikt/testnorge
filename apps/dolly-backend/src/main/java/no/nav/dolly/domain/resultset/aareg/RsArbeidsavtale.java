package no.nav.dolly.domain.resultset.aareg;

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
public class RsArbeidsavtale {

    private Integer antallKonverterteTimer;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'Arbeidstidsordninger'",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String arbeidstidsordning;

    private Double avtaltArbeidstimerPerUke;

    private String avloenningstype;

    @Schema(type = "LocalDateTime")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime endringsdatoStillingsprosent;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime sisteLoennsendringsdato;

    private Double stillingsprosent;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'Yrker'",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String yrke;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'AnsettelsesformAaareg'",
            type = "String")
    private String ansettelsesform;

    @Schema(type = "LocalDateTime")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime endringsdatoLoenn;
}
