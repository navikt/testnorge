package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(
        description = "Informasjon om opplysningspliktig eller arbeidsgiver (organisasjon eller person)",
        subTypes = { Organisasjon.class, Person.class }
)
@Getter
@Setter
@NoArgsConstructor
public abstract class OpplysningspliktigArbeidsgiver {

    @ApiModelProperty(
            notes = "Type: Organisasjon eller Person",
            allowableValues = "Organisasjon,Person"
    )
    public abstract String getType();
}
