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
@JsonPropertyOrder({ "type", "offentligIdent", "aktoerId" })
@ApiModel(
        description = "Informasjon om person (arbeidstaker/arbeidsgiver/opplysningspliktig)",
        parent = OpplysningspliktigArbeidsgiver.class
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Person extends OpplysningspliktigArbeidsgiver {

    @ApiModelProperty(
            notes = "Gjeldende offentlig ident",
            example = "31126700000"
    )
    private String offentligIdent;
    @ApiModelProperty(
            notes = "Akt&oslash;r-id",
            example = "1234567890"
    )
    private String aktoerId;

    @ApiModelProperty(
            notes = "Type: Person",
            allowableValues = "Person"
    )
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
