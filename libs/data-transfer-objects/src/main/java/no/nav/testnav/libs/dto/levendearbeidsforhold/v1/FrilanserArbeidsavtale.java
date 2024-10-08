package no.nav.testnav.libs.dto.levendearbeidsforhold.v1;

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
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for frilanser arbeidsforhold", allOf = Arbeidsavtale.class)
public class FrilanserArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Frilanser";

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
