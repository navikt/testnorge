package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsArbeidsgiver extends RsAktoer {
    @JsonProperty
    @Size(min = 9, max = 11)
    @ApiModelProperty(required = true, value = "Virksomhetsnummer eller offentilg ident for juridisk opplysningspliktig arbeidsgiver. 9/11 siffer", example = "001100110")
    private String virksomhetsnummer;
    @JsonProperty(defaultValue = "Utfylt med informasjon fra Aareg")
    @ApiModelProperty(value = "Kontaktinformasjon for arbeidsgiver.")
    private RsKontaktinformasjon kontaktinformasjon;

    @Override
    public String getArbeidsgiverId() {
        return virksomhetsnummer;
    }
}
