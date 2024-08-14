package no.nav.registre.testnorge.levendearbeidsforhold.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonPropertyOrder({
        "fartsomraade",
        "skipsregister",
        "fartoeystype",
        "arbeidstidsordning",
        "ansettelsesform",
        "yrke",
        "stillingsprosent",
        "antallTimerPrUke",
        "beregnetAntallTimerPrUke",
        "sistLoennsendring",
        "sistStillingsendring",
        "bruksperiode",
        "gyldighetsperiode",
        "sporingsinformasjon"
})
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for maritime arbeidsforhold", allOf = Arbeidsavtale.class)
public class MaritimArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Maritim";

    @Schema(description = "Fartsomr&aring;de (kodeverk: Fartsomraader)", example = "utenriks")
    private String fartsomraade;

    @Schema(description = "Skipsregister (kodeverk: Skipsregistre)", example = "nis")
    private String skipsregister;

    @Schema(description = "Skipstype (kodeverk: Skipstyper)", example = "turist")
    private String skipstype;

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
