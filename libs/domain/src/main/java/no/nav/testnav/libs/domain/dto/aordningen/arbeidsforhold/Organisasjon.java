package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "type", "organisasjonsnummer" })
@ApiModel(
        description = "Informasjon om organisasjon (arbeidsgiver/opplysningspliktig)",
        parent = OpplysningspliktigArbeidsgiver.class
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Organisasjon extends OpplysningspliktigArbeidsgiver {

    @ApiModelProperty(
            notes = "Organisasjonsnummer fra Enhetsregisteret",
            example = "987654321"
    )
    private String organisasjonsnummer;

    @ApiModelProperty(
            notes = "Type: Organisasjon",
            allowableValues = "Organisasjon"
    )
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
