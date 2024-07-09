package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonPropertyOrder({
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
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for ordin&aelig;re arbeidsforhold", allOf = Arbeidsavtale.class)
public class OrdinaerArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Ordinaer";

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
