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
@Schema(description = "Arbeidsavtale/ansettelsesdetaljer for forenklet oppgj&oslash;rsordning arbeidsforhold")
public class ForenkletOppgjoersordningArbeidsavtale extends Arbeidsavtale {

    public static final String TYPE = "Forenklet";

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
