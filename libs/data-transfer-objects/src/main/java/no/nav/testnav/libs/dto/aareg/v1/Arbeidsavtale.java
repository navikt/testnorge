package no.nav.testnav.libs.dto.aareg.v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrdinaerArbeidsavtale.class, name = OrdinaerArbeidsavtale.TYPE),
        @JsonSubTypes.Type(value = MaritimArbeidsavtale.class, name = MaritimArbeidsavtale.TYPE),
        @JsonSubTypes.Type(value = ForenkletOppgjoersordningArbeidsavtale.class, name = ForenkletOppgjoersordningArbeidsavtale.TYPE),
        @JsonSubTypes.Type(value = FrilanserArbeidsavtale.class, name = FrilanserArbeidsavtale.TYPE)
})
@Schema(description = "Informasjon om arbeidsavtale/ansettelsesdetaljer",
        subTypes = {
                OrdinaerArbeidsavtale.class,
                MaritimArbeidsavtale.class,
                ForenkletOppgjoersordningArbeidsavtale.class,
                FrilanserArbeidsavtale.class
        })
public abstract class Arbeidsavtale implements Arbeidsavtaletype {

    @Schema(description = "Arbeidstidsordning (kodeverk: Arbeidstidsordninger)", example = "ikkeSkift")
    private String arbeidstidsordning;

    @Schema(description = "Ansettelsesform (kodeverk: AnsettelsesformAareg)", example = "fast")
    private String ansettelsesform;

    @Schema(description = "Yrke (kodeverk: Yrker)", example = "2130123")
    private String yrke;

    @Schema(description = "Stillingsprosent", example = "49.5")
    private Double stillingsprosent;

    @Schema(description = "Antall timer per uke", example = "37.5")
    private Double antallTimerPrUke;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Beregnet antall timer per uke", example = "37.5")
    private Double beregnetAntallTimerPrUke;

    @Schema(description = "Dato for siste lønnsendring, format (ISO-8601): yyyy-MM-dd", example = "2014-07-15")
    private LocalDate sistLoennsendring;

    @Schema(description = "Dato for siste stillingsendring, format (ISO-8601): yyyy-MM-dd", example = "2015-12-15")
    private LocalDate sistStillingsendring;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Bruksperiode for arbeidsavtalen")
    private Bruksperiode bruksperiode;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Gyldighetsperiode for arbeidsavtalen")
    private Gyldighetsperiode gyldighetsperiode;
}
