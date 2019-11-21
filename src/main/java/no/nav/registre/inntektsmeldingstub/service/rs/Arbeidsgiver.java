package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@ApiModel
@Builder
@NoArgsConstructor
public class Arbeidsgiver {

    @JsonProperty
    @ApiModelProperty(required = true, value = "Virksomhetsnummer for arbeidsgiver, 9 siffer", example = "001100110")
    @Size(min = 9, max = 9)
    private String virksomhetsnummer;
    @JsonProperty
    @ApiModelProperty(required = true)
    private Kontaktinformasjon kontaktinformasjon;

}
