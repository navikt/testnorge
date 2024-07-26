package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.util.JavaTimeUtil;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = OrdinaerArbeidsavtale.TYPE, schema = OrdinaerArbeidsavtale.class),
                @DiscriminatorMapping(value = MaritimArbeidsavtale.TYPE, schema = MaritimArbeidsavtale.class),
                @DiscriminatorMapping(value = ForenkletOppgjoersordningArbeidsavtale.TYPE, schema = ForenkletOppgjoersordningArbeidsavtale.class),
                @DiscriminatorMapping(value = FrilanserArbeidsavtale.TYPE, schema = FrilanserArbeidsavtale.class)
        }
)
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

    @Schema(description = "Beregnet antall timer per uke", example = "37.5")
    private Double beregnetAntallTimerPrUke;

    @Schema(description = "Dato for siste l&oslash;nnsendring, format (ISO-8601): yyyy-MM-dd", example = "2014-07-15")
    private LocalDate sistLoennsendring;

    @Schema(description = "Dato for siste stillingsendring, format (ISO-8601): yyyy-MM-dd", example = "2015-12-15")
    private LocalDate sistStillingsendring;

    private Bruksperiode bruksperiode;

    private Gyldighetsperiode gyldighetsperiode;

    private Sporingsinformasjon sporingsinformasjon;

    @JsonIgnore
    public LocalDate getSistLoennsendring() {
        return sistLoennsendring;
    }

    @JsonIgnore
    public LocalDate getSistStillingsendring() {
        return sistStillingsendring;
    }

    @JsonProperty("sistLoennsendring")
    public String getSistLoennsendringAsString() {
        return JavaTimeUtil.toString(sistLoennsendring);
    }

    @JsonProperty("sistLoennsendring")
    public void setSistLoennsendringAsString(String sistLoennsendring) {
        this.sistLoennsendring = JavaTimeUtil.toLocalDate(sistLoennsendring);
    }

    @JsonProperty("sistStillingsendring")
    public String getSistStillingsendringAsString() {
        return JavaTimeUtil.toString(sistStillingsendring);
    }

    @JsonProperty("sistStillingsendring")
    public void setSistStillingsendringAsString(String sistStillingsendring) {
        this.sistStillingsendring = JavaTimeUtil.toLocalDate(sistStillingsendring);
    }
}
