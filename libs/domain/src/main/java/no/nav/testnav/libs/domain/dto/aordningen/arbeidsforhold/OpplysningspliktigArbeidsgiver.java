package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(
        description = "Informasjon om opplysningspliktig eller arbeidsgiver (organisasjon eller person)",
        subTypes = {Organisasjon.class, Person.class}
)
public interface OpplysningspliktigArbeidsgiver {

    @ApiModelProperty(
            notes = "Type: Organisasjon eller Person",
            allowableValues = "Organisasjon,Person"
    )
    String getType();
}
