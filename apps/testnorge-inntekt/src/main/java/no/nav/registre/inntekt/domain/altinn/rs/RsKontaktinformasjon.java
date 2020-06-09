package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;

@ApiModel
@Value
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class RsKontaktinformasjon {
    @JsonProperty
    @ApiModelProperty
    private String kontaktinformasjonNavn;
    @JsonProperty
    @Size(min = 8, max = 8)
    @ApiModelProperty(value = "Telefonnummer på 8-siffer uten mellomrom og landkode.", example = "12345678")
    private String telefonnummer;
}
