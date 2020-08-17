package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs;

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
@AllArgsConstructor
@Builder
public class RsArbeidsgiver {

    @JsonProperty
    @Size(min = 9, max = 11)
    @ApiModelProperty(required = true, value = "Virksomhetsnummer eller offentilg ident for juridisk opplysningspliktig arbeidsgiver. 9/11 siffer", example = "001100110")
    private String virksomhetsnummer;

    @JsonProperty(defaultValue = "Utfylt med informasjon fra Aareg")
    @ApiModelProperty(value = "Kontaktinformasjon for arbeidsgiver.")
    private RsKontaktinformasjon kontaktinformasjon;
}
