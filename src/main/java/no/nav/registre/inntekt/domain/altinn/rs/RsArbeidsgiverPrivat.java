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
public class RsArbeidsgiverPrivat extends RsAktoer {
    @JsonProperty
    @Size(min = 11, max = 11)
    @ApiModelProperty(value = "Arbeidsgivers fødselsnummer", required = true)
    private String arbeidsgiverFnr;
    @JsonProperty
    @ApiModelProperty(required = true)
    private RsKontaktinformasjon kontaktinformasjon;

    @Override
    public String getArbeidsgiverId() {
        return arbeidsgiverFnr;
    }
}
