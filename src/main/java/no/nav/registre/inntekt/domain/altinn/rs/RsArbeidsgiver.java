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
public class RsArbeidsgiver {
    @JsonProperty
    @Size(min = 9, max = 9)
    @ApiModelProperty(required = true, value = "Virksomhetsnummer for arbeidsgiver, 9 siffer", example = "001100110")
    private String virksomhetsnummer;
    @JsonProperty
    @ApiModelProperty(required = true)
    private RsKontaktinformasjon kontaktinformasjon;
}
