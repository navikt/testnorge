package no.nav.testnav.libs.dto.aareg.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Organisasjon.class, name = "Organisasjon"),
        @JsonSubTypes.Type(value = Person.class, name = "Person")
})
@Schema(description = "Informasjon om opplysningspliktig eller arbeidsgiver (organisasjon eller person)", subTypes = {Organisasjon.class, Person.class})
@SuppressWarnings("squid:S1610")
public abstract class OpplysningspliktigArbeidsgiver {

    @Schema(description = "Type: Organisasjon eller Person", allowableValues = "Organisasjon,Person")
    public abstract String getType();
}
