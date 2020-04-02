package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsKontaktinformasjon {
    @JsonProperty
    @ApiModelProperty(required = true)
    private String kontaktinformasjonNavn;
    @JsonProperty
    @Size(min = 8, max = 8)
    @ApiModelProperty(value = "Telefonnummer på 8-siffer uten mellomrom og landkode.", example = "12345678", required = true)
    private String telefonnummer;
}
