package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class RsArbeidsgiverPrivat {
    @JsonProperty
    @Size(min = 11, max = 11)
    @ApiModelProperty(value = "Arbeidsgivers fødselsnummer", required = true)
    private String arbeidsgiverFnr;
    @JsonProperty
    @ApiModelProperty(required = true)
    private RsKontaktinformasjon kontaktinformasjon;
}
