package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
public class RsKontaktinformasjon {

    @JsonProperty
    @ApiModelProperty(required = true)
    private String kontaktinformasjonNavn;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String telefonnummer;

}
